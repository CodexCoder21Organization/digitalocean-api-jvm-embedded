/**
 * Copyright (c) Jeevanandam M. (https://github.com/jeevatkm)
 *
 * <p>digitalocean-api-client source code and usage is governed by a MIT style license that can be
 * found in the LICENSE file
 */
package community.kotlin.contrib.digitalocean.embedded

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import community.kotlin.contrib.digitalocean.api.pojo.Volume
import java.lang.reflect.Type
import org.apache.commons.lang3.StringUtils

/**
 * Serialize the volume info for POST request.
 *
 * @author Eugene Strokin (https://github.com/strokine)
 * @since v2.7
 */
open class VolumeSerializer : JsonSerializer<Volume> {

  override fun serialize(volume: Volume, paramType: Type, context: JsonSerializationContext): JsonElement {
    val jsonObject: JsonObject = JsonObject()

    jsonObject.addProperty("id", volume.id)
    jsonObject.addProperty("name", volume.name)

    if (StringUtils.isNotBlank(volume.description)) {
      jsonObject.addProperty("description", volume.description)
    }

    if (StringUtils.isNotBlank(volume.region!!.slug)) {
      jsonObject.addProperty("region", volume.region!!.slug)
    }

    if (StringUtils.isNotBlank(volume.snapshotId)) {
      jsonObject.addProperty("snapshot_id", volume.snapshotId)
    }

    if (null != volume.size) {
      jsonObject.addProperty("size_gigabytes", volume.size)
    }

    // #89
    if (StringUtils.isNotBlank(volume.fileSystemType)) {
      jsonObject.addProperty("filesystem_type", volume.fileSystemType)
    }

    // #89
    if (StringUtils.isNotBlank(volume.fileSystemLabel)) {
      jsonObject.addProperty("filesystem_label", volume.fileSystemLabel)
    }

    // #89
    if (null != volume.tags && !volume.tags!!.isEmpty()) {
      var tags: JsonArray = JsonArray()
      for (tag in volume.tags!!) {
        tags.add(context.serialize(tag))
      }
      jsonObject.add("tags", tags)
    }

    return jsonObject
  }
}
