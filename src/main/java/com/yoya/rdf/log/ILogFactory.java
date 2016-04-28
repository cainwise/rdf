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

package com.yoya.rdf.log;

/**
 * Created by baihw on 16-4-14.
 *
 * 日志工厂规范接口
 */
public interface ILogFactory{

	/**
	 * 获取日志对象实例
	 *
	 * @param category 日志类别名称
	 * @return 日志对象
	 */
	public ILog getLog( String category );

}
