#Spring security 与 OAuth探索

## 1.简介
前记
```text
    总结使用spring security和Oauth来做认证鉴权的流程,
    作者在使用的过程中,查阅了很多资料和文档,也时长感到困惑不解,很多的文章逻辑让人感到摸不脉络
    好在经过一番探索,总算是有所收获,
    本着开源精神,如果能让处于学习security OAuth的朋友有所收获,那将是写这篇文章最大的收获.
```
针对场景
````text
    1. 前后端分离登录的认证
    2. 统一的网关认证
    3. spring security +Oauth2
````
花费时间
```text
    15分钟
```

## 2.spring security 
首先想想,如果给服务接口做认证,当然filter是很合适的了,spring security也是基于这种机制的.   
spring security对我来说就像一个黑匣子,  
**首先做的就是打开所有的日志调试信息(这步非常重要)**  
```
logging:
  level:
    org.springframework.security: debug
```
可以配合debug看到FilterChainProxy就是所有security过滤器链的代理了
```text
2019-08-15 11:55:30.423 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 1 of 14 in additional filter chain; firing Filter: 'WebAsyncManagerIntegrationFilter'
2019-08-15 11:55:30.423 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 2 of 14 in additional filter chain; firing Filter: 'SecurityContextPersistenceFilter'
2019-08-15 11:55:30.423 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 3 of 14 in additional filter chain; firing Filter: 'HeaderWriterFilter'
2019-08-15 11:55:30.423 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 4 of 14 in additional filter chain; firing Filter: 'LogoutFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 5 of 14 in additional filter chain; firing Filter: 'OAuth2AuthenticationProcessingFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 6 of 14 in additional filter chain; firing Filter: 'UsernamePasswordAuthenticationFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 7 of 14 in additional filter chain; firing Filter: 'DefaultLoginPageGeneratingFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 8 of 14 in additional filter chain; firing Filter: 'DefaultLogoutPageGeneratingFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 9 of 14 in additional filter chain; firing Filter: 'RequestCacheAwareFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 10 of 14 in additional filter chain; firing Filter: 'SecurityContextHolderAwareRequestFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 11 of 14 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 12 of 14 in additional filter chain; firing Filter: 'SessionManagementFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 13 of 14 in additional filter chain; firing Filter: 'ExceptionTranslationFilter'
2019-08-15 11:55:30.426 DEBUG 11240 --- [nio-8081-exec-6] o.s.security.web.FilterChainProxy        : /file/upload?resourceType=middle at position 14 of 14 in additional filter chain; firing Filter: 'FilterSecurityInterceptor'
```
下面仅仅挑选出我比较重点关注的几个过滤器
### 过滤器之 UsernamePasswordAuthenticationFilter
UsernamePasswordAuthenticationFilter 继承了 AbstractAuthenticationProcessingFilter 
过滤器的调用流程是由这个抽象类实现的doFilter,
阅读如下代码(已经简化)
```java
public abstract class AbstractAuthenticationProcessingFilter extends GenericFilterBean
		implements ApplicationEventPublisherAware, MessageSourceAware {
		
		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
        			throws IOException, ServletException {
        
        		HttpServletRequest request = (HttpServletRequest) req;
        		HttpServletResponse response = (HttpServletResponse) res;
        		
        		//根据配置中的url确定是否需要这个认证路径
        		if (!requiresAuthentication(request, response)) {
        		    //放行下一个过滤器
        			chain.doFilter(request, response);
        			return;
        		}
        		//认证信息
        		Authentication authResult;
        		try {
        		    //调用子类的认证方法(①)
        			authResult = attemptAuthentication(request, response);
        			if (authResult == null) {
        				// return immediately as subclass has indicated that it hasn't completed
        				// authentication
        				return;
        			}
        			sessionStrategy.onAuthentication(authResult, request, response);
        		}
        		catch (InternalAuthenticationServiceException|AuthenticationException failed) {
        			// Authentication failed 调用失败处理器
        			unsuccessfulAuthentication(request, response, failed);
        			return;
        		}
        		// Authentication success 认证成功
        		if (continueChainBeforeSuccessfulAuthentication) {
        			chain.doFilter(request, response);
        		}
                //调用成功处理器处理
        		successfulAuthentication(request, response, chain, authResult);
        	}
		
		}
```
继续阅读UsernamePasswordAuthenticationFilter中的attemptAuthentication()
```java
public class UsernamePasswordAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {
    
    	public Authentication attemptAuthentication(HttpServletRequest request,
    			HttpServletResponse response) throws AuthenticationException {
    	    
    	    //仅仅支持post
    		if (postOnly && !request.getMethod().equals("POST")) {
    			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
    		}
    		//获取用户名
    		String username = obtainUsername(request);
    		//获取密码
    		String password = obtainPassword(request);
            //~
            //~  
            //在这里根据接收到的参数生成未认证的 UsernamePasswordAuthenticationToken
    		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
    				username, password);
    
    		// Allow subclasses to set the "details" property
    		setDetails(request, authRequest);
            
    		//重点关注这个方法
    		//authenticate(②)
    		return this.getAuthenticationManager().authenticate(authRequest);
    	}
		}
```
继续阅读authenticate(authRequest)
```java

public interface AuthenticationManager {

	Authentication authenticate(Authentication authentication)
			throws AuthenticationException;
}

```
AuthenticationManager的authenticate()是security最顶级的认证方法
查看上一步方法中的具体实现

```java
public class ProviderManager implements AuthenticationManager, MessageSourceAware,
		InitializingBean {
    
    
    
		public Authentication authenticate(Authentication authentication)
        			throws AuthenticationException {
        		Class<? extends Authentication> toTest = authentication.getClass();
        		AuthenticationException lastException = null;
        		AuthenticationException parentException = null;
        		Authentication result = null;
        		Authentication parentResult = null;
        	
        
        		for (AuthenticationProvider provider : getProviders()) {
        		    
        		    //遍历support这种UsernamePasswordAuthenticationToken的 AuthenticationProvider去验证
        			if (!provider.supports(toTest)) {
        				continue;
        			}
        			try {
        			    //认证③
        			    //找出具体实现类AbstractUserDetailsAuthenticationProvider
        				result = provider.authenticate(authentication);
        				if (result != null) {
        					copyDetails(authentication, result);
        					break;
        				}
        			}
        			catch (AccountStatusException e) {
        			    //
        			}
        			catch (InternalAuthenticationServiceException e) {
        		
        		}
        
        }
        
        }
        }
      
```
继续阅读如下代码
```java
public abstract class AbstractUserDetailsAuthenticationProvider{
    
    public Authentication authenticate(Authentication authentication)
    			throws AuthenticationException {
            //断言该token是这个provider所能支持的
    		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
    				() -> messages.getMessage(
    						"AbstractUserDetailsAuthenticationProvider.onlySupports",
    						"Only UsernamePasswordAuthenticationToken is supported"));
    
    		// Determine username
    		String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
    				: authentication.getName();
            
    		boolean cacheWasUsed = true;
    		UserDetails user = this.userCache.getUserFromCache(username);
    
    		if (user == null) {
    			cacheWasUsed = false;
    
    			try {
    				//重要④
    			    user = retrieveUser(username,
    						(UsernamePasswordAuthenticationToken) authentication);
    			}
    			catch (UsernameNotFoundException notFound) {
    				logger.debug("User '" + username + "' not found");
    
                    //~
    
    		try {
    		    //校验是否冻结锁定
    		    preAuthenticationChecks.check(user);
    			additionalAuthenticationChecks(user,
    					(UsernamePasswordAuthenticationToken) authentication);
    		}
    		catch (AuthenticationException exception) {
    			//~
    		}
    
    		postAuthenticationChecks.check(user);
    
    		if (!cacheWasUsed) {
    			this.userCache.putUserInCache(user);
    		}
    
    		Object principalToReturn = user;
    
    		if (forcePrincipalAsString) {
    			principalToReturn = user.getUsername();
    		}
            //返回成功认证token的值
    		return createSuccessAuthentication(principalToReturn, authentication, user);
    	}
    	
    	//support支持token的类型
    
     public boolean supports(Class<?> authentication) {
             return (UsernamePasswordAuthenticationToken.class
                     .isAssignableFrom(authentication));
         }  } 
}
}
```
来看看子类DaoAuthenticationProvider的具体实现
```java
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider{
    
    	protected final UserDetails retrieveUser(String username,
    			UsernamePasswordAuthenticationToken authentication)
    			throws AuthenticationException {
    		prepareTimingAttackProtection();
    		try {
    		    //此处是调用UserDetailsService的loadUserByUsername
    			UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
    			if (loadedUser == null) {
    				throw new InternalAuthenticationServiceException(
    						"UserDetailsService returned null, which is an interface contract violation");
    			}
    			return loadedUser;
    		}
    		//~
    		catch (Exception ex) {
    		    //~~
    			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
    		}
    	}
}
```
到此为止一个UsernamePasswordAuthenticationFilter的完整流程才算是读完



