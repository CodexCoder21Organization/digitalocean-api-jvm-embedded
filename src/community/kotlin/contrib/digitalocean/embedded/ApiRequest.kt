/**
 * The MIT License
 *
 * <p>Copyright (c) 2013-2020 Jeevanandam M. (jeeva@myjeeva.com)
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package community.kotlin.contrib.digitalocean.embedded

import community.kotlin.contrib.digitalocean.api.common.ApiAction
import community.kotlin.contrib.digitalocean.api.common.RequestMethod

/**
 * Represents DigitalOcean API Request details
 *
 * @author Jeevanandam M. (jeeva@myjeeva.com)
 * @since v2.0
 */
open class ApiRequest {

  var apiAction: ApiAction? = null

  var data: Any? = null

  var pathParams: Array<Any>? = null

  var queryParams: Map<String, String>? = null

  var pageNo: Int? = null

  var perPage: Int? = null

  /** Default Constructor */
  constructor() {
    // Default Constructor
  }

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   */
  constructor(apiAction: ApiAction) : this(apiAction, null, null, null, null, null)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param pathParams a api request path variable value(s)
   */
  constructor(apiAction: ApiAction, pathParams: Array<Any>) : this(apiAction, null, pathParams, null, null, null)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param pageNo of the request pagination
   * @param perPage no. of items per page
   */
  constructor(apiAction: ApiAction, pageNo: Int?, perPage: Int?) : this(apiAction, null, null, pageNo, null, perPage)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param pageNo of the request pagination
   * @param queryParams of the api request
   * @param perPage no. of items per page
   */
  constructor(apiAction: ApiAction, pageNo: Int?, queryParams: Map<String, String>?, perPage: Int?) : this(apiAction, null, null, pageNo, queryParams, perPage)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param data a api request body data object
   */
  constructor(apiAction: ApiAction, data: Any) : this(apiAction, data, null, null, null, null)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param pathParams a api request path variable value(s)
   * @param pageNo of the request pagination
   * @param perPage no. of items per page
   */
  constructor(apiAction: ApiAction, pathParams: Array<Any>, pageNo: Int?, perPage: Int?) : this(apiAction, null, pathParams, pageNo, null, perPage)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param data a api request body data object
   * @param pathParams a api request path variable value(s)
   */
  constructor(apiAction: ApiAction, data: Any, pathParams: Array<Any>) : this(apiAction, data, pathParams, null, null, null)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param data a api request body data object
   * @param pathParams a api request path variable value(s)
   * @param pageNo of the request pagination
   * @param queryParams of the api request
   * @param perPage no. of items per page
   */
  constructor(apiAction: ApiAction, data: Any?, pathParams: Array<Any>?, pageNo: Int?, queryParams: Map<String, String>?, perPage: Int?) {
    this.apiAction = apiAction
    this.data = data
    this.pathParams = pathParams
    this.pageNo = pageNo
    this.queryParams = queryParams
    this.perPage = perPage
  }

  fun isCollectionElement(): Boolean {
    return getElementName()?.endsWith("s") ?: false
  }

  /** @return the path */
  fun getPath(): String {
    return apiAction!!.path
  }

  /** @return the elementName */
  fun getElementName(): String? {
    return apiAction!!.elementName
  }

  /** @return the method */
  fun getMethod(): RequestMethod {
    return apiAction!!.method
  }

  /** @return the clazz */
  fun getClazz(): Class<*>? {
    return apiAction!!.clazz
  }


}
