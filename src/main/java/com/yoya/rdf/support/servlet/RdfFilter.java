/*
 *  Copyright (c) 2016, baihw (javakf@163.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.yoya.rdf.support.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;
import com.yoya.rdf.Rdf;
import com.yoya.rdf.router.IResponse;
import com.yoya.rdf.router.impl.SimpleResponse;

/**
 * Created by baihw on 16-4-15.
 *
 * 其它环境支持--servlet环境提供Filter接入支持。
 */
public class RdfFilter implements Filter{

//	// 上下文环境路径长度。
//	private int		_contextPathLen	= -1;
	// 请求路径在路由时需要跳过的字符长度。
	private int		_pathSkipLen	= -1;

	// 忽略的请求地址。
	private String	_ignoreUrl		= null;

	@Override
	public void init( FilterConfig filterConfig ) throws ServletException{

		// 预设允许开发人员配置的系统参数
		String routeWorkBase = filterConfig.getInitParameter( "routeWorkBase" );
		if( null == routeWorkBase || 0 == ( routeWorkBase = routeWorkBase.trim() ).length() )
			throw new RuntimeException( "routeWorkBase参数必须正确设置！" );
		System.setProperty( "rdf.routeWorkBase", routeWorkBase );
		System.out.println( "RdfFilter.init::routeWorkBase => " + routeWorkBase );

		// 如果将框架当作插件使用与其它框架进行集成时，由于拦截的不是根路径，所以需要计算路由时需要跳过的字符长度。
		// 此处暂时未做自动发现拦截路径的逻辑，所以暂由开发人员手工配置需要跳过的字符长度。
		// 如： 拦截路径为：/do，则跳过字符长度为3。
		String pathSkipLen = filterConfig.getInitParameter( "pathSkipLen" );
		if( null == pathSkipLen || 0 == ( pathSkipLen = pathSkipLen.trim() ).length() ){
			_pathSkipLen = 0;
		}else{
			_pathSkipLen = Integer.parseInt( pathSkipLen );
		}
		System.out.println( "RdfFilter.init::pathSkipLen => " + _pathSkipLen );

		// 开发人员配置的忽略请求地址。当使用了servlet容器时，通常不需要自己处理静态文件的访问请求，所以应该配置静态文件为忽略路径。
		// 一个示例的静态文件地址忽略配置如： “.+(?i)\.(html|css|js|json|ico|png|gif|woff|map)$”
		String ignoreUrl = filterConfig.getInitParameter( "ignoreUrl" );
		if( null != ignoreUrl && 0 != ( ignoreUrl = ignoreUrl.trim() ).length() ){
			this._ignoreUrl = ignoreUrl;
		}
		System.out.println( "RdfFilter.init::ignoreUrl => " + ignoreUrl );

		// 调用框架初始化动作。
		Rdf.me().init();

	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException{

		HttpServletRequest req = ( HttpServletRequest )request;
		HttpServletResponse res = ( HttpServletResponse )response;

		// 获取请求的资源相对路径。
		String requestPath = req.getServletPath(); // req.getRequestURI();

		if( null != _ignoreUrl && ( "/".equals( requestPath ) || requestPath.matches( _ignoreUrl ) ) ){
			// 忽略根路径及开发人员指定的忽略请求路径。
			chain.doFilter( request, response );
			return;
		}

		req.setCharacterEncoding( Rdf.getEncoding() );
		res.setCharacterEncoding( Rdf.getEncoding() );

//		// 拼装basePath,存入request属性中,方便页面直接使用.
//		String path = req.getContextPath();
//		String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + path + "/";
//		req.setAttribute( "__BASEPATH__", basePath );

//		// 计算上下文环境路径长度。
//		if( -1 == _contextPathLen ){
//			String contextPath = req.getServletContext().getContextPath();
//			_contextPathLen = ( null == contextPath || "/".equals( contextPath ) ? 0 : contextPath.length() );
//			_pathSkipLen += _contextPathLen;
//		}

		// 计算框架中使用的实际请求路径
		if( 0 < _pathSkipLen ){
			requestPath = requestPath.substring( _pathSkipLen );
		}

		// 创建框架适配的请求、响应对象。
		HttpServletRequestWrapper ireq = new HttpServletRequestWrapper( req );
		ireq.setPath( requestPath );
		IResponse ires = new SimpleResponse();

		// 调用框架路由请求处理逻辑。
		Rdf.me().route( ireq, ires );

		// 设置响应代码
		res.setStatus( ires.getStatus() );

		// 设置响应头信息
		ires.getHeader().forEach( ( key, value ) -> {
			res.setHeader( key, value );
		} );

		// 根据响应数据类型进行响应处理。
		IResponse.Type resType = ires.getDataType();
		if( null == resType ){
			resType = IResponse.Type.TEXT;
		}

		// 根据响应数据类型设置指定的头信息。
		if( !ires.hasHeader( IResponse.HEAD_CONTENT_TYPE ) )
			res.setHeader( IResponse.HEAD_CONTENT_TYPE, resType.getContentType() );

		// 下载响应特殊处理
		if( IResponse.Type.STREAM == resType ){
			try( InputStream inStream = ires.getDataInputStream(); ServletOutputStream outStream = response.getOutputStream(); ){
				ByteStreams.copy( inStream, outStream );
				outStream.flush();
			}
		}else{
			// 禁止浏览器缓存
			res.setHeader( "Pragma", "no-cache" );
			res.setHeader( "Cache-Control", "no-cache" );
			res.setDateHeader( "Expires", 0 );

			try( PrintWriter writer = response.getWriter(); ){
				writer.write( ires.getData() );
				writer.flush();
			}
		}

	}

	@Override
	public void destroy(){
	}

}