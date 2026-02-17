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
import community.kotlin.contrib.digitalocean.api.pojo.ForwardingRules
import community.kotlin.contrib.digitalocean.api.pojo.LoadBalancer
import java.lang.reflect.Type

/**
 * Serialize the load balancer info for POST request.
 *
 * @author Thomas Lehoux (https://github.com/tlehoux)
 * @since v2.11
 */
open class LoadBalancerSerializer : JsonSerializer<LoadBalancer> {

  override fun serialize(loadBalancer: LoadBalancer, paramType: Type, context: JsonSerializationContext): JsonElement {
    val jsonObject: JsonObject = JsonObject()

    jsonObject.addProperty("name", loadBalancer.name)

    jsonObject.addProperty("region", loadBalancer.region!!.slug)

    if (null != loadBalancer.algorithm) {
      jsonObject.addProperty("algorithm", loadBalancer.algorithm.toString())
    }

    if (null != loadBalancer.forwardingRules && !loadBalancer.forwardingRules!!.isEmpty()) {
      var rules: JsonArray = JsonArray()
      for (rule in loadBalancer.forwardingRules!!) {
        rules.add(context.serialize(rule))
      }
      jsonObject.add("forwarding_rules", rules)
    }

    if (null != loadBalancer.healthCheck) {
      jsonObject.add("health_check", context.serialize(loadBalancer.healthCheck))
    }

    if (null != loadBalancer.stickySessions) {
      jsonObject.add("sticky_sessions", context.serialize(loadBalancer.stickySessions))
    }

    if (null != loadBalancer.dropletIds && !loadBalancer.dropletIds!!.isEmpty()) {
      var dropletIds: JsonArray = JsonArray()
      for (dropletId in loadBalancer.dropletIds!!) {
        dropletIds.add(context.serialize(dropletId))
      }
      jsonObject.add("droplet_ids", dropletIds)
    }
    return jsonObject
  }
}
