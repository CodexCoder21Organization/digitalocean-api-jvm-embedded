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
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

/**
 * Represents DigitalOcean API Response details
 *
 * @author Jeevanandam M. (jeeva@myjeeva.com)
 * @since v2.0
 */
open class ApiResponse {

  var apiAction: ApiAction? = null

  var data: Any? = null

  var requestSuccess: Boolean = false

  /** Default Constructor */
  constructor() {
    // Default constructor
  }

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param requestSuccess result of the executed api request
   */
  constructor(apiAction: ApiAction, requestSuccess: Boolean) : this(apiAction, null, requestSuccess)

  /**
   * Constructor
   *
   * @param apiAction a info about api request
   * @param data a api response object
   * @param requestSuccess result of the executed api request
   */
  constructor(apiAction: ApiAction, data: Any?, requestSuccess: Boolean) {
    this.apiAction = apiAction
    this.data = data
    this.requestSuccess = requestSuccess
  }

  override fun toString(): String {
    return ReflectionToStringBuilder.toString(this)
  }

  fun isDataExists(): Boolean {
    return (null == data)
  }


}
