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

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import community.kotlin.contrib.digitalocean.api.pojo.Droplet
import community.kotlin.contrib.digitalocean.api.pojo.Key
import java.lang.reflect.Type
import org.apache.commons.lang3.StringUtils

/**
 * Serializer for droplet class
 *
 * @author Jeevanandam M. (jeeva@myjeeva.com)
 * @since v2.0
 */
open class DropletSerializer : JsonSerializer<Droplet> {

  override fun serialize(droplet: Droplet, paramType: Type, context: JsonSerializationContext): JsonElement {
    val jsonObject: JsonObject = JsonObject()

    jsonObject.addProperty("name", droplet.name)

    if (null != droplet.names && !droplet.names!!.isEmpty()) {
      var names: JsonArray = JsonArray()
      for (name in droplet.names!!) {
        names.add(context.serialize(name))
      }
      jsonObject.add("names", names)
    }

    jsonObject.addProperty("region", droplet.region!!.slug)
    jsonObject.addProperty("size", droplet.size)

    if (null == droplet.image!!.id) {
      jsonObject.addProperty("image", droplet.image!!.slug)
    } else {
      jsonObject.addProperty("image", droplet.image!!.id)
    }

    if (null != droplet.enableBackup) {
      jsonObject.addProperty("backups", droplet.enableBackup)
    }

    if (null != droplet.enableIpv6) {
      jsonObject.addProperty("ipv6", droplet.enableIpv6)
    }

    if (null != droplet.enablePrivateNetworking) {
      jsonObject.addProperty("private_networking", droplet.enablePrivateNetworking)
    }

    if (null != droplet.keys && !droplet.keys!!.isEmpty()) {
      var sshKeys: JsonArray = JsonArray()
      for (k in droplet.keys!!) {
        if (null != k.id) {
          sshKeys.add(context.serialize(k.id))
        }
        if (!StringUtils.isBlank(k.fingerprint)) {
          sshKeys.add(context.serialize(k.fingerprint))
        }
      }
      jsonObject.add("ssh_keys", sshKeys)
    }

    // #19
    if (null != droplet.userData) {
      jsonObject.addProperty("user_data", droplet.userData)
    }

    // #56
    if (null != droplet.volumeIds && !droplet.volumeIds!!.isEmpty()) {
      var volumes: JsonArray = JsonArray()
      for (volume in droplet.volumeIds!!) {
        volumes.add(context.serialize(volume))
      }
      jsonObject.add("volumes", volumes)
    }

    // #56
    if (null != droplet.tags && !droplet.tags!!.isEmpty()) {
      var tags: JsonArray = JsonArray()
      for (tag in droplet.tags!!) {
        tags.add(context.serialize(tag))
      }
      jsonObject.add("tags", tags)
    }

    // #70
    if (null != droplet.installMonitoring) {
      jsonObject.addProperty("monitoring", droplet.installMonitoring)
    }

    return jsonObject
  }
}
