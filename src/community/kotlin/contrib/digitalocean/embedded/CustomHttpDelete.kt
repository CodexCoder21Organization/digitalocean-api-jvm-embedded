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

import community.kotlin.contrib.digitalocean.api.DigitalOcean
import java.net.URI
import org.apache.http.client.methods.HttpPost

/**
 * Custom delete HTTP client method with Payload support.
 *
 * <p>To begin with {@link DigitalOcean#untagResources(String, java.util.List)} method needs Payload
 * support.
 *
 * @author Jeevanandam M. (jeeva@myjeeva.com)
 * @since v2.5
 */
open class CustomHttpDelete : HttpPost {

  constructor(uri: URI) : super(uri)

  constructor(url: String) : super(url)

  override fun getMethod(): String {
    return "DELETE"
  }
}
