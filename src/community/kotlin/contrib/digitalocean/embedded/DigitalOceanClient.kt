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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import community.kotlin.contrib.digitalocean.api.DigitalOcean
import community.kotlin.contrib.digitalocean.api.common.ActionType
import community.kotlin.contrib.digitalocean.api.common.ApiAction
import community.kotlin.contrib.digitalocean.api.common.Constants
import community.kotlin.contrib.digitalocean.api.common.RequestMethod
import community.kotlin.contrib.digitalocean.api.exception.DigitalOceanException
import community.kotlin.contrib.digitalocean.api.exception.RequestUnsuccessfulException
import community.kotlin.contrib.digitalocean.embedded.CustomHttpDelete
import community.kotlin.contrib.digitalocean.api.pojo.Account
import community.kotlin.contrib.digitalocean.api.pojo.Action
import community.kotlin.contrib.digitalocean.api.pojo.Actions
import community.kotlin.contrib.digitalocean.api.pojo.Backups
import community.kotlin.contrib.digitalocean.api.pojo.Certificate
import community.kotlin.contrib.digitalocean.api.pojo.Certificates
import community.kotlin.contrib.digitalocean.api.pojo.Delete
import community.kotlin.contrib.digitalocean.api.pojo.Domain
import community.kotlin.contrib.digitalocean.api.pojo.DomainRecord
import community.kotlin.contrib.digitalocean.api.pojo.DomainRecords
import community.kotlin.contrib.digitalocean.api.pojo.Domains
import community.kotlin.contrib.digitalocean.api.pojo.Droplet
import community.kotlin.contrib.digitalocean.api.pojo.DropletAction
import community.kotlin.contrib.digitalocean.api.pojo.Droplets
import community.kotlin.contrib.digitalocean.api.pojo.Firewall
import community.kotlin.contrib.digitalocean.api.pojo.Firewalls
import community.kotlin.contrib.digitalocean.api.pojo.FloatingIP
import community.kotlin.contrib.digitalocean.api.pojo.FloatingIPAction
import community.kotlin.contrib.digitalocean.api.pojo.FloatingIPs
import community.kotlin.contrib.digitalocean.api.pojo.ForwardingRules
import community.kotlin.contrib.digitalocean.api.pojo.HealthCheck
import community.kotlin.contrib.digitalocean.api.pojo.Image
import community.kotlin.contrib.digitalocean.api.pojo.ImageAction
import community.kotlin.contrib.digitalocean.api.pojo.Images
import community.kotlin.contrib.digitalocean.api.pojo.Kernels
import community.kotlin.contrib.digitalocean.api.pojo.Key
import community.kotlin.contrib.digitalocean.api.pojo.Keys
import community.kotlin.contrib.digitalocean.api.pojo.LoadBalancer
import community.kotlin.contrib.digitalocean.api.pojo.LoadBalancers
import community.kotlin.contrib.digitalocean.api.pojo.Neighbors
import community.kotlin.contrib.digitalocean.api.pojo.Project
import community.kotlin.contrib.digitalocean.api.pojo.Projects
import community.kotlin.contrib.digitalocean.api.pojo.Regions
import community.kotlin.contrib.digitalocean.api.pojo.Resource
import community.kotlin.contrib.digitalocean.api.pojo.Resources
import community.kotlin.contrib.digitalocean.api.pojo.Response
import community.kotlin.contrib.digitalocean.api.pojo.Sizes
import community.kotlin.contrib.digitalocean.api.pojo.Snapshot
import community.kotlin.contrib.digitalocean.api.pojo.Snapshots
import community.kotlin.contrib.digitalocean.api.pojo.Tag
import community.kotlin.contrib.digitalocean.api.pojo.Tags
import community.kotlin.contrib.digitalocean.api.pojo.Volume
import community.kotlin.contrib.digitalocean.api.pojo.VolumeAction
import community.kotlin.contrib.digitalocean.api.pojo.Volumes
import community.kotlin.contrib.digitalocean.embedded.DropletSerializer
import community.kotlin.contrib.digitalocean.embedded.FirewallSerializer
import community.kotlin.contrib.digitalocean.embedded.LoadBalancerSerializer
import community.kotlin.contrib.digitalocean.embedded.VolumeSerializer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.commons.lang3.StringUtils
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.ParseException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * DigitalOcean API client wrapper methods Implementation
 *
 * @author Jeevanandam M. (jeeva@myjeeva.com)
 */
open class DigitalOceanClient : DigitalOcean, Constants {


  /** Http client */
  var httpClient: CloseableHttpClient? = null

  /** OAuth Authorization Token for Accessing DigitalOcean API */
  var authToken: String? = null

  /** DigitalOcean API version. defaults to v2 from constructor */
  var apiVersion: String? = null

  /** DigitalOcean API Host is <code>api.digitalocean.com</code> */
  var apiHost: String = "api.digitalocean.com"

  /** Gson Parser instance for deserialize */
  var deserialize: Gson? = null

  /** Gson Parser instance for serialize */
  var serialize: Gson? = null

  /** API Request Header */
  var requestHeaders: Array<Header>? = null

  /**
   * DigitalOcean Client Constructor
   *
   * @param authToken a {@link String} object
   */
  constructor(authToken: String) : this("v2", authToken)

  /**
   * DigitalOcean Client Constructor
   *
   * @param apiVersion a {@link String} object
   * @param authToken a {@link String} object
   */
  constructor(apiVersion: String, authToken: String) : this(apiVersion, authToken, null)

  /**
   * DigitalOcean Client Constructor
   *
   * @param apiVersion a {@link String} object
   * @param authToken a {@link String} object
   * @param httpClient a {@link CloseableHttpClient} object
   */
  constructor(apiVersion: String, authToken: String, httpClient: CloseableHttpClient?) {

    if (!"v2".equals(apiVersion, ignoreCase = true)) {
      throw IllegalArgumentException("Only API version 2 is supported.")
    }

    this.apiVersion = apiVersion
    this.authToken = authToken
    this.httpClient = httpClient
    initialize()
  }


  // =======================================
  // Droplet access/manipulation methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableDroplets(pageNo: Int, perPage: Int): Droplets {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.AVAILABLE_DROPLETS, pageNo, perPage)).data as Droplets
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDropletKernels(dropletId: Int, pageNo: Int, perPage: Int): Kernels {
    validateDropletIdAndPageNo(dropletId, pageNo)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.GET_DROPLETS_KERNELS, params, pageNo, perPage)).data as Kernels
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDropletSnapshots(dropletId: Int, pageNo: Int, perPage: Int): Snapshots {
    validateDropletIdAndPageNo(dropletId, pageNo)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.GET_DROPLET_SNAPSHOTS, params, pageNo, perPage)).data as Snapshots
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDropletBackups(dropletId: Int, pageNo: Int, perPage: Int): Backups {
    validateDropletIdAndPageNo(dropletId, pageNo)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.GET_DROPLET_BACKUPS, params, pageNo, perPage)).data as Backups
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDropletInfo(dropletId: Int): Droplet {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.GET_DROPLET_INFO, params)).data as Droplet
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createDroplet(droplet: Droplet): Droplet {
    if (null == droplet
        || StringUtils.isBlank(droplet.name)
        || null == droplet.region
        || null == droplet.size
        || (null == droplet.image
            || (null == droplet.image!!.id && null == droplet.image!!.slug))) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Region Slug, Size Slug, Image Id/Slug] for create droplet.")
    }

    return perform(ApiRequest(ApiAction.CREATE_DROPLET, droplet)).data as Droplet
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createDroplets(droplet: Droplet): Droplets {
    if (null == droplet
        || (null == droplet.names || droplet.names!!.isEmpty())
        || null == droplet.region
        || null == droplet.size
        || (null == droplet.image
            || (null == droplet.image!!.id && null == droplet.image!!.slug))) {
      throw IllegalArgumentException(
          "Missing required parameters [Names, Region Slug, Size Slug, Image Id/Slug] for creating multiple droplets.")
    }

    if (StringUtils.isNotBlank(droplet.name)) {
      throw IllegalArgumentException(
          "Name parameter is not allowed, while creating multiple droplet instead use 'names' attributes.")
    }

    return perform(ApiRequest(ApiAction.CREATE_DROPLETS, droplet)).data as Droplets
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteDroplet(dropletId: Int): Delete {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.DELETE_DROPLET, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteDropletByTagName(tagName: String): Delete {
    checkBlankAndThrowError(tagName, "Missing required parameter - tagName.")

    val queryParams: MutableMap<String, String> = HashMap()
    queryParams.put("tag_name", tagName)
    return perform(ApiRequest(ApiAction.DELETE_DROPLET_BY_TAG_NAME, null, queryParams, null))
            .data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDropletNeighbors(dropletId: Int, pageNo: Int): Droplets {
    validateDropletIdAndPageNo(dropletId, pageNo)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.GET_DROPLET_NEIGHBORS, params, pageNo, null)).data as Droplets
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAllDropletNeighbors(pageNo: Int): Neighbors {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.ALL_DROPLET_NEIGHBORS, pageNo)).data as Neighbors
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableDropletsByTagName(tagName: String, pageNo: Int, perPage: Int): Droplets {
    checkBlankAndThrowError(tagName, "Missing required parameter - tagName.")
    validatePageNo(pageNo)

    val queryParams: MutableMap<String, String> = HashMap()
    queryParams.put("tag_name", tagName)

    return perform(ApiRequest(ApiAction.AVAILABLE_DROPLETS, pageNo, queryParams, perPage))
            .data as Droplets
  }

  // Droplet action methods

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun rebootDroplet(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.REBOOT_DROPLET, DropletAction(ActionType.REBOOT), params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun powerCycleDroplet(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.POWER_CYCLE_DROPLET,
                    DropletAction(ActionType.POWER_CYCLE),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun shutdownDroplet(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.SHUTDOWN_DROPLET, DropletAction(ActionType.SHUTDOWN), params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun powerOffDroplet(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.POWER_OFF_DROPLET, DropletAction(ActionType.POWER_OFF), params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun powerOnDroplet(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.POWER_ON_DROPLET, DropletAction(ActionType.POWER_ON), params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun resetDropletPassword(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.RESET_DROPLET_PASSWORD,
                    DropletAction(ActionType.PASSWORD_RESET),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun resizeDroplet(dropletId: Int, size: String): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.RESIZE)
    action.size = size
    return perform(ApiRequest(ApiAction.RESIZE_DROPLET, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun resizeDroplet(dropletId: Int, size: String, disk: Boolean): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.RESIZE)
    action.size = size
    action.disk = disk
    return perform(ApiRequest(ApiAction.RESIZE_DROPLET, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun takeDropletSnapshot(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.SNAPSHOT_DROPLET, DropletAction(ActionType.SNAPSHOT), params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun takeDropletSnapshot(dropletId: Int, snapshotName: String): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.SNAPSHOT)
    action.name = snapshotName
    return perform(ApiRequest(ApiAction.SNAPSHOT_DROPLET, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun restoreDroplet(dropletId: Int, imageId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.RESTORE)
    action.image = imageId
    return perform(ApiRequest(ApiAction.RESTORE_DROPLET, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun rebuildDroplet(dropletId: Int, imageId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.REBUILD)
    action.image = imageId
    return perform(ApiRequest(ApiAction.REBUILD_DROPLET, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun enableDropletBackups(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.ENABLE_DROPLET_BACKUPS,
                    DropletAction(ActionType.ENABLE_BACKUPS),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun disableDropletBackups(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.DISABLE_DROPLET_BACKUPS,
                    DropletAction(ActionType.DISABLE_BACKUPS),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun renameDroplet(dropletId: Int, name: String): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.RENAME)
    action.name = name
    return perform(ApiRequest(ApiAction.RENAME_DROPLET, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun changeDropletKernel(dropletId: Int, kernelId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    var action: DropletAction = DropletAction(ActionType.CHANGE_KERNEL)
    action.kernel = kernelId
    return perform(ApiRequest(ApiAction.CHANGE_DROPLET_KERNEL, action, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun enableDropletIpv6(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.ENABLE_DROPLET_IPV6,
                    DropletAction(ActionType.ENABLE_IPV6),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun enableDropletPrivateNetworking(dropletId: Int): Action {
    validateDropletId(dropletId)

    val params = arrayOf<Any>(dropletId)
    return perform(
                ApiRequest(
                    ApiAction.ENABLE_DROPLET_PRIVATE_NETWORKING,
                    DropletAction(ActionType.ENABLE_PRIVATE_NETWORKING),
                    params))
            .data as Action
  }

  // ==============================================
  // Account manipulation/access methods
  // ==============================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAccountInfo(): Account {
    return perform(ApiRequest(ApiAction.GET_ACCOUNT_INFO)).data as Account
  }

  // ==============================================
  // Actions manipulation/access methods
  // ==============================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableActions(pageNo: Int, perPage: Int): Actions {
    validatePageNo(pageNo)
    return perform(ApiRequest(ApiAction.AVAILABLE_ACTIONS, pageNo, perPage)).data as Actions
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getActionInfo(actionId: Int): Action {
    checkNullAndThrowError(actionId, "Missing required parameter - actionId")

    val params = arrayOf<Any>(actionId)
    return perform(ApiRequest(ApiAction.GET_ACTION_INFO, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableDropletActions(dropletId: Int, pageNo: Int, perPage: Int): Actions {
    validateDropletIdAndPageNo(dropletId, pageNo)

    val params = arrayOf<Any>(dropletId)
    return perform(ApiRequest(ApiAction.GET_DROPLET_ACTIONS, params, pageNo, perPage)).data as Actions
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableImageActions(imageId: Int, pageNo: Int, perPage: Int): Actions {
    checkNullAndThrowError(imageId, "Missing required parameter - imageId.")
    validatePageNo(pageNo)

    val params = arrayOf<Any>(imageId)
    return perform(ApiRequest(ApiAction.GET_IMAGE_ACTIONS, params, pageNo, perPage)).data as Actions
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableFloatingIPActions(ipAddress: String, pageNo: Int, perPage: Int): Actions {
    checkBlankAndThrowError(ipAddress, "Missing required parameter - ipAddress.")
    validatePageNo(pageNo)

    val params = arrayOf<Any>(ipAddress)
    return perform(ApiRequest(ApiAction.GET_FLOATING_IP_ACTIONS, params, pageNo, perPage))
            .data as Actions
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getFloatingIPActionInfo(ipAddress: String, actionId: Int): Action {
    checkBlankAndThrowError(ipAddress, "Missing required parameter - ipAddress.")
    checkNullAndThrowError(actionId, "Missing required parameter - actionId.")

    val params = arrayOf<Any>(ipAddress, actionId)
    return perform(ApiRequest(ApiAction.GET_FLOATING_IP_ACTION_INFO, params)).data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableVolumeActions(volumeId: String): Actions {
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")

    val params = arrayOf<Any>(volumeId)
    return perform(ApiRequest(ApiAction.GET_VOLUME_ACTIONS, params)).data as Actions
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getVolumeAction(volumeId: String, actionId: Int): Action {
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")
    checkNullAndThrowError(actionId, "Missing required parameter - actionId.")

    val params = arrayOf<Any>(volumeId, actionId)
    return perform(ApiRequest(ApiAction.GET_VOLUME_ACTION, params)).data as Action
  }

  // =======================================
  // Images access/manipulation methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableImages(pageNo: Int, perPage: Int): Images {
    validatePageNo(pageNo)
    return perform(ApiRequest(ApiAction.AVAILABLE_IMAGES, pageNo, perPage)).data as Images
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableImages(pageNo: Int, perPage: Int, type: ActionType): Images {
    validatePageNo(pageNo)

    var qp: MutableMap<String, String>
    if (ActionType.DISTRIBUTION.equals(type) || ActionType.APPLICATION.equals(type)) {
      qp = HashMap()
      qp.put("type", type.toString())
    } else {
      throw DigitalOceanException(
          "Incorrect type value [Allowed: DISTRIBUTION or APPLICATION].")
    }

    return perform(ApiRequest(ApiAction.AVAILABLE_IMAGES, pageNo, qp, perPage)).data as Images
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getUserImages(pageNo: Int, perPage: Int): Images {
    validatePageNo(pageNo)
    val qp: MutableMap<String, String> = HashMap()
    qp.put("private", "true")
    return perform(ApiRequest(ApiAction.AVAILABLE_IMAGES, pageNo, qp, perPage)).data as Images
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getImageInfo(imageId: Int): Image {
    checkNullAndThrowError(imageId, "Missing required parameter - imageId.")

    val params = arrayOf<Any>(imageId)
    return perform(ApiRequest(ApiAction.GET_IMAGE_INFO, params)).data as Image
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getImageInfo(slug: String): Image {
    checkBlankAndThrowError(slug, "Missing required parameter - slug.")

    val params = arrayOf<Any>(slug)
    return perform(ApiRequest(ApiAction.GET_IMAGE_INFO, params)).data as Image
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createCustomImage(image: Image): Image {
    if (null == image
        || StringUtils.isBlank(image.name)
        || StringUtils.isBlank(image.url)
        || StringUtils.isBlank(image.region)) {
      throw IllegalArgumentException(
          "Missing required parameter to create custom image [name, url, or region].")
    }

    return perform(ApiRequest(ApiAction.CREATE_CUSTOM_IMAGE, image)).data as Image
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateImage(image: Image): Image {
    if (null == image || null == image.name) {
      throw IllegalArgumentException("Missing required parameter - image object.")
    }

    val params = arrayOf<Any>(image.id!!)
    return perform(ApiRequest(ApiAction.UPDATE_IMAGE_INFO, image, params)).data as Image
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteImage(imageId: Int): Delete {
    checkNullAndThrowError(imageId, "Missing required parameter - imageId.")

    val params = arrayOf<Any>(imageId)
    return perform(ApiRequest(ApiAction.DELETE_IMAGE, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun transferImage(imageId: Int, regionSlug: String): Action {
    checkNullAndThrowError(imageId, "Missing required parameter - imageId.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    val params = arrayOf<Any>(imageId)
    return perform(
                ApiRequest(
                    ApiAction.TRANSFER_IMAGE,
                    ImageAction(ActionType.TRANSFER, regionSlug),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun convertImage(imageId: Int): Action {
    checkNullAndThrowError(imageId, "Missing required parameter - imageId.")

    val params = arrayOf<Any>(imageId)
    return perform(
                ApiRequest(
                    ApiAction.CONVERT_IMAGE, ImageAction(ActionType.CONVERT), params))
            .data as Action
  }

  // =======================================
  // Regions methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableRegions(pageNo: Int): Regions {
    validatePageNo(pageNo)
    return perform(ApiRequest(ApiAction.AVAILABLE_REGIONS, pageNo, DEFAULT_PAGE_SIZE)).data as Regions
  }

  // =======================================
  // Sizes methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableSizes(pageNo: Int): Sizes {
    validatePageNo(pageNo)
    return perform(ApiRequest(ApiAction.AVAILABLE_SIZES, pageNo, DEFAULT_PAGE_SIZE)).data as Sizes
  }

  // =======================================
  // Domain methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableDomains(pageNo: Int): Domains {
    return perform(ApiRequest(ApiAction.AVAILABLE_DOMAINS, pageNo, DEFAULT_PAGE_SIZE)).data as Domains
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDomainInfo(domainName: String): Domain {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")

    val params = arrayOf<Any>(domainName)
    return perform(ApiRequest(ApiAction.GET_DOMAIN_INFO, params)).data as Domain
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createDomain(domain: Domain): Domain {
    checkBlankAndThrowError(domain.name!!, "Missing required parameter - domainName.")
    // #89 - removed the validation in-favor of
    // https://developers.digitalocean.com/documentation/changelog/api-v2/create-domains-without-providing-an-ip-address/
    // checkBlankAndThrowError(domain.ipAddress, "Missing required parameter - ipAddress.");

    return perform(ApiRequest(ApiAction.CREATE_DOMAIN, domain)).data as Domain
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteDomain(domainName: String): Delete {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")

    val params = arrayOf<Any>(domainName)
    return perform(ApiRequest(ApiAction.DELETE_DOMAIN, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDomainRecords(domainName: String, pageNo: Int, perPage: Int): DomainRecords {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")

    val params = arrayOf<Any>(domainName)
    return perform(ApiRequest(ApiAction.GET_DOMAIN_RECORDS, params, pageNo, perPage)).data as DomainRecords
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDomainRecordInfo(domainName: String, recordId: Int): DomainRecord {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")
    checkNullAndThrowError(recordId, "Missing required parameter - recordId.")

    val params = arrayOf<Any>(domainName, recordId)
    return perform(ApiRequest(ApiAction.GET_DOMAIN_RECORD_INFO, params)).data as DomainRecord
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createDomainRecord(domainName: String, domainRecord: DomainRecord): DomainRecord {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")
    checkNullAndThrowError(domainRecord, "Missing required parameter - domainRecord")

    val params = arrayOf<Any>(domainName)
    return perform(ApiRequest(ApiAction.CREATE_DOMAIN_RECORD, domainRecord, params)).data as DomainRecord
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateDomainRecord(domainName: String, recordId: Int, domainRecord: DomainRecord): DomainRecord {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")
    checkNullAndThrowError(recordId, "Missing required parameter - recordId.")
    checkNullAndThrowError(domainRecord, "Missing required parameter - domainRecord")

    val params = arrayOf<Any>(domainName, recordId)
    return perform(ApiRequest(ApiAction.UPDATE_DOMAIN_RECORD, domainRecord, params)).data as DomainRecord
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteDomainRecord(domainName: String, recordId: Int): Delete {
    checkBlankAndThrowError(domainName, "Missing required parameter - domainName.")
    checkNullAndThrowError(recordId, "Missing required parameter - recordId.")

    val params = arrayOf<Any>(domainName, recordId)
    return perform(ApiRequest(ApiAction.DELETE_DOMAIN_RECORD, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableKeys(pageNo: Int): Keys {
    return perform(ApiRequest(ApiAction.AVAILABLE_KEYS, pageNo, DEFAULT_PAGE_SIZE)).data as Keys
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getKeyInfo(sshKeyId: Int): Key {
    checkNullAndThrowError(sshKeyId, "Missing required parameter - sshKeyId.")

    val params = arrayOf<Any>(sshKeyId)
    return perform(ApiRequest(ApiAction.GET_KEY_INFO, params)).data as Key
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getKeyInfo(fingerprint: String): Key {
    checkBlankAndThrowError(fingerprint, "Missing required parameter - fingerprint.")

    val params = arrayOf<Any>(fingerprint)
    return perform(ApiRequest(ApiAction.GET_KEY_INFO, params)).data as Key
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createKey(newKey: Key): Key {
    checkNullAndThrowError(newKey, "Missing required parameter - newKey")
    checkBlankAndThrowError(newKey.name!!, "Missing required parameter - name.")
    checkBlankAndThrowError(newKey.publicKey!!, "Missing required parameter - publicKey.")

    return perform(ApiRequest(ApiAction.CREATE_KEY, newKey)).data as Key
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateKey(sshKeyId: Int, newSshKeyName: String): Key {
    checkNullAndThrowError(sshKeyId, "Missing required parameter - sshKeyId.")
    checkBlankAndThrowError(newSshKeyName, "Missing required parameter - newSshKeyName.")

    val params = arrayOf<Any>(sshKeyId)
    return perform(ApiRequest(ApiAction.UPDATE_KEY, Key(newSshKeyName), params)).data as Key
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateKey(fingerprint: String, newSshKeyName: String): Key {
    checkBlankAndThrowError(fingerprint, "Missing required parameter - fingerprint.")
    checkBlankAndThrowError(newSshKeyName, "Missing required parameter - newSshKeyName.")

    val params = arrayOf<Any>(fingerprint)
    return perform(ApiRequest(ApiAction.UPDATE_KEY, Key(newSshKeyName), params)).data as Key
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteKey(sshKeyId: Int): Delete {
    checkNullAndThrowError(sshKeyId, "Missing required parameter - sshKeyId.")

    val params = arrayOf<Any>(sshKeyId)
    return perform(ApiRequest(ApiAction.DELETE_KEY, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteKey(fingerprint: String): Delete {
    checkBlankAndThrowError(fingerprint, "Missing required parameter - fingerprint.")

    val params = arrayOf<Any>(fingerprint)
    return perform(ApiRequest(ApiAction.DELETE_KEY, params)).data as Delete
  }

  // =======================================
  // Floating IPs methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableFloatingIPs(pageNo: Int, perPage: Int): FloatingIPs {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.FLOATING_IPS, pageNo, perPage)).data as FloatingIPs
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createFloatingIP(dropletId: Int): FloatingIP {
    validateDropletId(dropletId)

    return perform(ApiRequest(ApiAction.CREATE_FLOATING_IP, FloatingIPAction(dropletId)))
            .data as FloatingIP
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createFloatingIP(region: String): FloatingIP {
    checkBlankAndThrowError(region, "Missing required parameter - region.")

    return perform(ApiRequest(ApiAction.CREATE_FLOATING_IP, FloatingIPAction(region)))
            .data as FloatingIP
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getFloatingIPInfo(ipAddress: String): FloatingIP {
    checkBlankAndThrowError(ipAddress, "Missing required parameter - ipAddress.")

    val params = arrayOf<Any>(ipAddress)
    return perform(ApiRequest(ApiAction.GET_FLOATING_IP_INFO, params)).data as FloatingIP
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteFloatingIP(ipAddress: String): Delete {
    checkBlankAndThrowError(ipAddress, "Missing required parameter - ipAddress.")

    val params = arrayOf<Any>(ipAddress)
    return perform(ApiRequest(ApiAction.DELETE_FLOATING_IP, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun assignFloatingIP(dropletId: Int, ipAddress: String): Action {
    validateDropletId(dropletId)
    checkBlankAndThrowError(ipAddress, "Missing required parameter - ipAddress.")

    val params = arrayOf<Any>(ipAddress)
    return perform(
                ApiRequest(
                    ApiAction.ASSIGN_FLOATING_IP,
                    FloatingIPAction(dropletId, "assign"),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun unassignFloatingIP(ipAddress: String): Action {
    checkBlankAndThrowError(ipAddress, "Missing required parameter - ipAddress.")

    val params = arrayOf<Any>(ipAddress)
    return perform(
                ApiRequest(
                    ApiAction.UNASSIGN_FLOATING_IP, FloatingIPAction(null, null, "unassign"), params))
            .data as Action
  }

  // =======================================
  // Tags methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableTags(pageNo: Int, perPage: Int): Tags {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.AVAILABLE_TAGS, pageNo, perPage)).data as Tags
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createTag(name: String): Tag {
    checkBlankAndThrowError(name, "Missing required parameter - tag name")

    return perform(ApiRequest(ApiAction.CREATE_TAG, Tag(name))).data as Tag
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getTag(name: String): Tag {
    checkBlankAndThrowError(name, "Missing required parameter - tag name")

    val params = arrayOf<Any>(name)
    return perform(ApiRequest(ApiAction.GET_TAG, params)).data as Tag
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteTag(name: String): Delete {
    checkBlankAndThrowError(name, "Missing required parameter - tag name")

    val params = arrayOf<Any>(name)
    return perform(ApiRequest(ApiAction.DELETE_TAG, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun tagResources(name: String, resources: List<Resource>): Response {
    checkBlankAndThrowError(name, "Missing required parameter - tag name")
    if (null == resources || resources.isEmpty()) {
      throw IllegalArgumentException("Missing required parameter - list of resources for tag")
    }

    val params = arrayOf<Any>(name)
    return perform(ApiRequest(ApiAction.TAG_RESOURCE, Resources(resources), params)).data as Response
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun untagResources(name: String, resources: List<Resource>): Response {
    checkBlankAndThrowError(name, "Missing required parameter - tag name")
    if (null == resources || resources.isEmpty()) {
      throw IllegalArgumentException(
          "Missing required parameter - list of resources for untag")
    }

    val params = arrayOf<Any>(name)
    return perform(ApiRequest(ApiAction.UNTAG_RESOURCE, Resources(resources), params))
            .data as Response
  }

  // =======================================
  // Volume access/manipulation methods
  // =======================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableVolumes(regionSlug: String): Volumes {
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    val data: MutableMap<String, String> = HashMap()
    data.put("region", regionSlug)
    return perform(ApiRequest(ApiAction.AVAILABLE_VOLUMES, data)).data as Volumes
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createVolume(volume: Volume): Volume {
    if (null == volume
        || StringUtils.isBlank(volume.name)
        || null == volume.region
        || null == volume.size) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Region Slug, Size] for create volume.")
    }

    return perform(ApiRequest(ApiAction.CREATE_VOLUME, volume)).data as Volume
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getVolumeInfo(volumeId: String): Volume {
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")

    val params = arrayOf<Any>(volumeId)
    return perform(ApiRequest(ApiAction.GET_VOLUME_INFO, params)).data as Volume
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getVolumeInfo(volumeName: String, regionSlug: String): Volumes {
    checkBlankAndThrowError(volumeName, "Missing required parameter - volumeName.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    val data: MutableMap<String, String> = HashMap()
    data.put("region", regionSlug)
    data.put("name", volumeName)
    return perform(ApiRequest(ApiAction.GET_VOLUME_INFO_BY_NAME, data)).data as Volumes
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteVolume(volumeId: String): Delete {
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")

    val params = arrayOf<Any>(volumeId)
    return perform(ApiRequest(ApiAction.DELETE_VOLUME, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteVolume(volumeName: String, regionSlug: String): Delete {
    checkBlankAndThrowError(volumeName, "Missing required parameter - volumeName.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    val data: MutableMap<String, String> = HashMap()
    data.put("region", regionSlug)
    data.put("name", volumeName)
    return perform(ApiRequest(ApiAction.DELETE_VOLUME_BY_NAME, data)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun attachVolume(dropletId: Int, volumeId: String, regionSlug: String): Action {
    validateDropletId(dropletId)
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    val params = arrayOf<Any>(volumeId)
    return perform(
                ApiRequest(
                    ApiAction.ACTIONS_VOLUME,
                    VolumeAction(ActionType.ATTACH, dropletId, regionSlug),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun attachVolumeByName(dropletId: Int, volumeName: String, regionSlug: String): Action {
    validateDropletId(dropletId)
    checkBlankAndThrowError(volumeName, "Missing required parameter - volumeName.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    return perform(
                ApiRequest(
                    ApiAction.ACTIONS_VOLUME_BY_NAME,
                    VolumeAction(ActionType.ATTACH, dropletId, regionSlug, volumeName, null)))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun detachVolume(dropletId: Int, volumeId: String, regionSlug: String): Action {
    validateDropletId(dropletId)
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    val params = arrayOf<Any>(volumeId)
    return perform(
                ApiRequest(
                    ApiAction.ACTIONS_VOLUME,
                    VolumeAction(ActionType.DETACH, dropletId, regionSlug),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun detachVolumeByName(dropletId: Int, volumeName: String, regionSlug: String): Action {
    validateDropletId(dropletId)
    checkBlankAndThrowError(volumeName, "Missing required parameter - volumeName.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    return perform(
                ApiRequest(
                    ApiAction.ACTIONS_VOLUME_BY_NAME,
                    VolumeAction(ActionType.DETACH, dropletId, regionSlug, volumeName, null)))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun resizeVolume(volumeId: String, regionSlug: String, sizeGigabytes: Double): Action {
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")
    checkBlankAndThrowError(regionSlug, "Missing required parameter - regionSlug.")

    if (null == sizeGigabytes) {
      throw IllegalArgumentException("Missing required parameter - sizeGigabytes.")
    }

    val params = arrayOf<Any>(volumeId)
    return perform(
                ApiRequest(
                    ApiAction.ACTIONS_VOLUME,
                    VolumeAction(ActionType.RESIZE, regionSlug, sizeGigabytes),
                    params))
            .data as Action
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getVolumeSnapshots(volumeId: String, pageNo: Int, perPage: Int): Snapshots {
    validatePageNo(pageNo)
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")

    val params = arrayOf<Any>(volumeId)
    return perform(ApiRequest(ApiAction.GET_VOLUME_SNAPSHOTS, params, pageNo, perPage)).data as Snapshots
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun takeVolumeSnapshot(volumeId: String, snapshotName: String): Snapshot {
    checkBlankAndThrowError(volumeId, "Missing required parameter - volumeId.")
    checkBlankAndThrowError(snapshotName, "Missing required parameter - snapshotName.")

    val data: MutableMap<String, String> = HashMap()
    data.put("name", snapshotName)

    val params = arrayOf<Any>(volumeId)
    return perform(ApiRequest(ApiAction.SNAPSHOT_VOLUME, data, params)).data as Snapshot
  }

  // ===========================================
  // Snapshots manipulation methods
  // ===========================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableSnapshots(pageNo: Int, perPage: Int): Snapshots {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.AVAILABLE_SNAPSHOTS, pageNo, perPage)).data as Snapshots
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAllDropletSnapshots(pageNo: Int, perPage: Int): Snapshots {
    validatePageNo(pageNo)

    val qp: MutableMap<String, String> = HashMap()
    qp.put("resource_type", "droplet")

    return perform(ApiRequest(ApiAction.ALL_DROPLET_SNAPSHOTS, pageNo, qp, perPage)).data as Snapshots
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAllVolumeSnapshots(pageNo: Int, perPage: Int): Snapshots {
    validatePageNo(pageNo)

    val qp: MutableMap<String, String> = HashMap()
    qp.put("resource_type", "volume")

    return perform(ApiRequest(ApiAction.ALL_VOLUME_SNAPSHOTS, pageNo, qp, perPage)).data as Snapshots
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getSnaphotInfo(snapshotId: String): Snapshot {
    validateSnapshotId(snapshotId)

    val params = arrayOf<Any>(snapshotId)
    return perform(ApiRequest(ApiAction.GET_SNAPSHOT_INFO, params)).data as Snapshot
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteSnapshot(snapshotId: String): Delete {
    validateSnapshotId(snapshotId)

    val params = arrayOf<Any>(snapshotId)
    return perform(ApiRequest(ApiAction.DELETE_SNAPSHOT, params)).data as Delete
  }

  // ===========================================
  // Load balancers manipulation methods
  // ===========================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createLoadBalancer(loadBalancer: LoadBalancer): LoadBalancer {
    if (null == loadBalancer
        || StringUtils.isBlank(loadBalancer.name)
        || null == loadBalancer.region) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Region Slug] for create loadBalancer.")
    }
    validateForwardingRules(loadBalancer.forwardingRules!!)
    validateHealthCheck(loadBalancer.healthCheck!!)

    return perform(ApiRequest(ApiAction.CREATE_LOAD_BALANCER, loadBalancer)).data as LoadBalancer
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getLoadBalancerInfo(loadBalancerId: String): LoadBalancer {
    validateLoadBalancerId(loadBalancerId)

    val params = arrayOf<Any>(loadBalancerId)
    return perform(ApiRequest(ApiAction.GET_LOAD_BALANCER_INFO, params)).data as LoadBalancer
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableLoadBalancers(pageNo: Int, perPage: Int): LoadBalancers {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.AVAILABLE_LOAD_BALANCERS, pageNo, perPage)).data as LoadBalancers
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateLoadBalancer(loadBalancer: LoadBalancer): LoadBalancer {
    if (null == loadBalancer
        || StringUtils.isBlank(loadBalancer.id)
        || StringUtils.isBlank(loadBalancer.name)
        || null == loadBalancer.region) {
      throw IllegalArgumentException(
          "Missing required parameters [Id, Name, Region Slug] for update loadBalancer.")
    }
    validateForwardingRules(loadBalancer.forwardingRules!!)
    validateHealthCheck(loadBalancer.healthCheck!!)

    val params = arrayOf<Any>(loadBalancer.id!!)

    return perform(ApiRequest(ApiAction.UPDATE_LOAD_BALANCER, loadBalancer, params)).data as LoadBalancer
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun addDropletsToLoadBalancer(loadBalancerId: String, dropletIds: List<Int>): Response {
    validateLoadBalancerId(loadBalancerId)

    if (null == dropletIds || dropletIds.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [dropletIds].")
    }

    val params = arrayOf<Any>(loadBalancerId)
    val data: MutableMap<String, List<Int>> = HashMap()
    data.put("droplet_ids", dropletIds)
    return perform(ApiRequest(ApiAction.ADD_DROPLET_TO_LOAD_BALANCER, data, params)).data as Response
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun removeDropletsFromLoadBalancer(loadBalancerId: String, dropletIds: List<Int>): Delete {
    validateLoadBalancerId(loadBalancerId)

    if (null == dropletIds || dropletIds.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [dropletIds].")
    }

    val params = arrayOf<Any>(loadBalancerId)
    val data: MutableMap<String, List<Int>> = HashMap()
    data.put("droplet_ids", dropletIds)
    return perform(ApiRequest(ApiAction.REMOVE_DROPLET_FROM_LOAD_BALANCER, data, params))
            .data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun addForwardingRulesToLoadBalancer(loadBalancerId: String, forwardingRules: List<ForwardingRules>): Response {
    validateLoadBalancerId(loadBalancerId)

    if (null == forwardingRules || forwardingRules.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [forwardingRules].")
    }

    val params = arrayOf<Any>(loadBalancerId)
    val data: MutableMap<String, List<ForwardingRules>> = HashMap()
    data.put("forwarding_rules", forwardingRules)
    return perform(ApiRequest(ApiAction.ADD_FORWARDING_RULES_TO_LOAD_BALANCER, data, params))
            .data as Response
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun removeForwardingRulesFromLoadBalancer(loadBalancerId: String, forwardingRules: List<ForwardingRules>): Delete {
    validateLoadBalancerId(loadBalancerId)

    if (null == forwardingRules || forwardingRules.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [forwardingRules].")
    }

    val params = arrayOf<Any>(loadBalancerId)
    val data: MutableMap<String, List<ForwardingRules>> = HashMap()
    data.put("forwarding_rules", forwardingRules)
    return perform(ApiRequest(ApiAction.REMOVE_FORWARDING_RULES_FROM_LOAD_BALANCER, data, params))
            .data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteLoadBalancer(loadBalancerId: String): Delete {
    validateLoadBalancerId(loadBalancerId)

    val params = arrayOf<Any>(loadBalancerId)
    return perform(ApiRequest(ApiAction.DELETE_LOAD_BALANCER, params)).data as Delete
  }

  // ===========================================
  // Certificates manipulation methods
  // ===========================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableCertificates(pageNo: Int, perPage: Int): Certificates {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.AVAILABLE_CERTIFICATES, pageNo, perPage)).data as Certificates
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createCertificate(certificate: Certificate): Certificate {
    if (null == certificate
        || StringUtils.isBlank(certificate.name)
        || StringUtils.isBlank(certificate.privateKey)
        || StringUtils.isBlank(certificate.leafCertificate)
        || StringUtils.isBlank(certificate.certificateChain)) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Private Key, Leaf Certificate, Certificate Chain] for create certificate.")
    }

    return perform(ApiRequest(ApiAction.CREATE_CERTIFICATE, certificate)).data as Certificate
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createLetsEncryptCertificate(certificate: Certificate): Certificate {
    if (null == certificate
        || StringUtils.isBlank(certificate.name)
        || StringUtils.isBlank(certificate.type)
        || certificate.type != "lets_encrypt"
        || certificate.dnsNames == null
        || certificate.dnsNames!!.isEmpty()) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Type(lets_encrypt), List of DNS Names] for create Let's Encrypt certificate.")
    }

    return perform(ApiRequest(ApiAction.CREATE_CERTIFICATE, certificate)).data as Certificate
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getCertificateInfo(certificateId: String): Certificate {
    checkBlankAndThrowError(certificateId, "Missing required parameter - certificateId.")

    val params = arrayOf<Any>(certificateId)
    return perform(ApiRequest(ApiAction.GET_CERTIFICATE_INFO, params)).data as Certificate
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteCertificate(certificateId: String): Delete {
    checkBlankAndThrowError(certificateId, "Missing required parameter - certificateId.")

    val params = arrayOf<Any>(certificateId)
    return perform(ApiRequest(ApiAction.DELETE_CERTIFICATE, params)).data as Delete
  }

  // ===========================================
  // Firewall manipulation methods
  // ===========================================

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createFirewall(firewall: Firewall): Firewall {
    if (null == firewall
        || StringUtils.isBlank(firewall.name)
        || firewall.inboundRules?.isEmpty() != false
        || firewall.outboundRules?.isEmpty() != false) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Inbound rules, Outbound rules] for create firewall.")
    }
    return perform(ApiRequest(ApiAction.CREATE_FIREWALL, firewall)).data as Firewall
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getFirewallInfo(firewallId: String): Firewall {
    checkBlankAndThrowError(
        firewallId, "Missing required parameters [FirewallID] for get firewall info.")

    val params = arrayOf<Any>(firewallId)
    return perform(ApiRequest(ApiAction.GET_FIREWALL_INFO, params)).data as Firewall
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateFirewall(firewall: Firewall): Firewall {
    if (null == firewall
        || StringUtils.isBlank(firewall.name)
        || firewall.inboundRules?.isEmpty() != false
        || firewall.outboundRules?.isEmpty() != false) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Inbound rules, Outbound rules] for update firewall info.")
    }

    val params = arrayOf<Any>(firewall.id!!)
    return perform(ApiRequest(ApiAction.UPDATE_FIREWALL, firewall, params)).data as Firewall
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteFirewall(firewallId: String): Delete {
    checkBlankAndThrowError(
        firewallId, "Missing required parameters [ID] for delete firewall info.")

    val params = arrayOf<Any>(firewallId)
    return perform(ApiRequest(ApiAction.DELETE_FIREWALL, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun addDropletsToFirewall(firewallId: String, dropletIds: List<Int>): Response {
    checkBlankAndThrowError(firewallId, "Missing required parameter [firewallId].")

    if (null == dropletIds || dropletIds.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [dropletIds].")
    }

    val params = arrayOf<Any>(firewallId)
    val data: MutableMap<String, List<Int>> = HashMap()
    data.put("droplet_ids", dropletIds)
    return perform(ApiRequest(ApiAction.ADD_DROPLET_TO_FIREWALL, data, params)).data as Response
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun removeDropletsFromFirewall(firewallId: String, dropletIds: List<Int>): Delete {
    checkBlankAndThrowError(firewallId, "Missing required parameter [firewallId].")

    if (null == dropletIds || dropletIds.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [dropletIds].")
    }

    val params = arrayOf<Any>(firewallId)
    val data: MutableMap<String, List<Int>> = HashMap()
    data.put("droplet_ids", dropletIds)
    return perform(ApiRequest(ApiAction.REMOVE_DROPLET_FROM_FIREWALL, data, params)).data as Delete
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableFirewalls(pageNo: Int, perPage: Int): Firewalls {
    validatePageNo(pageNo)

    return perform(ApiRequest(ApiAction.AVAILABLE_FIREWALLS, pageNo, perPage)).data as Firewalls
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun createProject(project: Project): Project {

    if (null == project || StringUtils.isBlank(project.name) || null == project.purpose) {
      throw IllegalArgumentException("Missing required parameters [Name, Purpose].")
    }

    return perform(ApiRequest(ApiAction.CREATE_PROJECT, project)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getAvailableProjects(): Projects {
    return perform(ApiRequest(ApiAction.GET_ALL_PROJECTS)).data as Projects
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateProject(project: Project): Project {

    if (null == project
        || StringUtils.isBlank(project.name)
        || StringUtils.isBlank(project.description)
        || StringUtils.isBlank(project.purpose)) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Description, Purpose].")
    }

    val params = arrayOf<Any>(project.id!!)

    return perform(ApiRequest(ApiAction.UPDATE_PROJECT, project, params)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun patchProject(project: Project): Project {

    if (null == project
        || StringUtils.isBlank(project.name)
        || StringUtils.isBlank(project.description)
        || StringUtils.isBlank(project.purpose)) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Description, Purpose].")
    }

    val params = arrayOf<Any>(project.id!!)

    return perform(ApiRequest(ApiAction.PATCH_PROJECT, project, params)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getProject(projectId: String): Project {

    validateProjectId(projectId)

    val params = arrayOf<Any>(projectId)
    return perform(ApiRequest(ApiAction.GET_PROJECT, params)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun getDefaultProject(): Project {
    return perform(ApiRequest(ApiAction.GET_DEFAULT_PROJECT)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun updateDefaultProject(project: Project): Project {

    if (null == project
        || StringUtils.isBlank(project.name)
        || StringUtils.isBlank(project.description)
        || StringUtils.isBlank(project.purpose)) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Description, Purpose].")
    }

    return perform(ApiRequest(ApiAction.UPDATE_DEFAULT_PROJECT, project)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun patchDefaultProject(project: Project): Project {

    if (null == project
        || StringUtils.isBlank(project.name)
        || StringUtils.isBlank(project.description)
        || StringUtils.isBlank(project.purpose)) {
      throw IllegalArgumentException(
          "Missing required parameters [Name, Description, Purpose].")
    }

    return perform(ApiRequest(ApiAction.PATCH_DEFAULT_PROJECT, project)).data as Project
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  override fun deleteProject(projectId: String): Delete {

    checkBlankAndThrowError(projectId, "Missing required parameter - projectId.")

    val params = arrayOf<Any>(projectId)
    return perform(ApiRequest(ApiAction.DELETE_PROJECT, params)).data as Delete
  }

  //
  // Private methods
  //

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun perform(request: ApiRequest): ApiResponse {

    var uri: URI = createUri(request)
    var response: String? = null

    if (RequestMethod.GET == request.getMethod()) {
      response = doGet(uri)
    } else if (RequestMethod.POST == request.getMethod()) {
      response = doPost(uri, createRequestData(request)!!)
    } else if (RequestMethod.PUT == request.getMethod()) {
      response = doPut(uri, createRequestData(request)!!)
    } else if (RequestMethod.DELETE == request.getMethod()) {
      response = doDelete(uri, createRequestData(request)!!)
    } else if (RequestMethod.PATCH == request.getMethod()) {
      response = doPatch(uri, createRequestData(request)!!)
    }

    var apiResponse: ApiResponse = ApiResponse(request.apiAction!!, true)

    try {
      if (request.isCollectionElement()) {
        apiResponse.data = deserialize!!.fromJson(response, request.getClazz())
      } else {
        var rootObject: JsonObject = JsonParser.parseString(response).asJsonObject
        var elementObject: JsonObject = rootObject.get(request.getElementName()).asJsonObject
        fetchAddElement(RATE_LIMIT_ELEMENT_NAME, rootObject, elementObject)
        fetchAddElement(LINKS_ELEMENT_NAME, rootObject, elementObject)
        fetchAddElement(META_ELEMENT_NAME, rootObject, elementObject)
        apiResponse.data = deserialize!!.fromJson(elementObject, request.getClazz())
      }
    } catch (jse: JsonSyntaxException) {
      log.error("Error occurred while parsing response", jse)
      apiResponse.requestSuccess = false
    }

    log.debug("API Response:: " + apiResponse.toString())

    return apiResponse
  }

  fun fetchAddElement(key: String, rootObject: JsonObject, elementObject: JsonObject) {
    var ele: JsonElement = rootObject.get(key)
    if (null != ele) {
      elementObject.add(key, ele)
    }
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun doGet(uri: URI): String {
    var get: HttpGet = HttpGet(uri)
    get.setHeaders(requestHeaders)
    return executeHttpRequest(get)
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun doPost(uri: URI, entity: HttpEntity): String {
    var post: HttpPost = HttpPost(uri)
    post.setHeaders(requestHeaders)

    if (null != entity) {
      post.setEntity(entity)
    }

    return executeHttpRequest(post)
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun doPut(uri: URI, entity: HttpEntity): String {
    var put: HttpPut = HttpPut(uri)
    put.setHeaders(requestHeaders)

    if (null != entity) {
      put.setEntity(entity)
    }

    return executeHttpRequest(put)
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun doDelete(uri: URI, entity: HttpEntity): String {
    if (null == entity) {
      var delete: HttpDelete = HttpDelete(uri)
      delete.setHeaders(requestHeaders)
      delete.setHeader("Content-Type", JSON_CONTENT_TYPE)
      return executeHttpRequest(delete)
    }

    var delete: CustomHttpDelete = CustomHttpDelete(uri)
    delete.setHeaders(requestHeaders)
    delete.setEntity(entity)
    return executeHttpRequest(delete)
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun doPatch(uri: URI, entity: HttpEntity): String {
    var patch: HttpPatch = HttpPatch(uri)
    patch.setHeaders(requestHeaders)

    if (null != entity) {
      patch.setEntity(entity)
    }

    return executeHttpRequest(patch)
  }

  @Throws(DigitalOceanException::class, RequestUnsuccessfulException::class)
  fun executeHttpRequest(request: HttpUriRequest): String {
    log.debug("HTTP Request:: {} {}", request.getMethod(), request.getURI())
    var response: String = ""
    var httpResponse: CloseableHttpResponse? = null

    try {
      httpResponse = httpClient!!.execute(request)
      log.debug("HTTP Response Object:: {}", httpResponse)

      response = appendRateLimitValues(evaluateResponse(httpResponse), httpResponse)
      log.debug("Parsed Response:: {}", response)
    } catch (ioe: IOException) {
      throw RequestUnsuccessfulException(ioe.message ?: "", ioe)
    } finally {
      try {
        if (null != httpResponse) {
          httpResponse.close()
        }
      } catch (e: IOException) {
        // Ignoring close exception, really no impact.
        // Since response object is 99.999999% success rate
        // this is nothing to do with DigitalOcean, its
        // typical handling of HttpClient request/response
        log.error("Error occurred while closing a response.", e)
      }
    }

    return response
  }

  @Throws(DigitalOceanException::class)
  fun evaluateResponse(httpResponse: HttpResponse): String {
    var statusCode: Int = httpResponse.statusLine.statusCode
    var response: String = ""

    if (HttpStatus.SC_OK == statusCode
        || HttpStatus.SC_CREATED == statusCode
        || HttpStatus.SC_ACCEPTED == statusCode) {
      response = httpResponseToString(httpResponse)
    } else if (HttpStatus.SC_NO_CONTENT == statusCode) {
      // in a way its always true from client perspective if there is no exception.
      response = String.format(NO_CONTENT_JSON_STRUCT, statusCode)
    }

    if (statusCode >= 400 && statusCode < 510) {
      var jsonStr: String = httpResponseToString(httpResponse)
      log.debug("JSON Response: {}", jsonStr)

      var jsonObj: JsonObject? = null
      var errorMsg: String = StringUtils.EMPTY
      var id: String = StringUtils.EMPTY
      try {
        jsonObj = JsonParser.parseString(jsonStr).asJsonObject
        id = jsonObj.get("id").asString
        errorMsg = jsonObj.get("message").asString
      } catch (e: JsonSyntaxException) {
        errorMsg = "Digital Oceans server are on maintenance. Wait for official messages " +
                "from digital ocean itself. Such as 'Cloud Control Panel, API & Support Ticket System Unavailable'"
      }

      var errorMsgFull: String = String.format("\nHTTP Status Code: %s\nError Id: %s\nError Message: %s", statusCode, id, errorMsg)
      log.debug(errorMsgFull)

      throw DigitalOceanException(errorMsg, id, statusCode)
    }

    return response
  }

  fun httpResponseToString(httpResponse: HttpResponse): String {
    var response: String = StringUtils.EMPTY
    if (null != httpResponse.entity) {
      try {
        response = EntityUtils.toString(httpResponse.entity, UTF_8)
      } catch (pe: ParseException) {
        log.error(pe.message, pe)
      } catch (ioe: IOException) {
        log.error(ioe.message, ioe)
      }
    }
    return response
  }

  fun createUri(request: ApiRequest): URI {
    var ub: URIBuilder = URIBuilder()
    ub.setScheme(HTTPS_SCHEME)
    ub.setHost(apiHost)
    ub.setPath(createPath(request))

    if (null != request.pageNo) {
      ub.setParameter(PARAM_PAGE_NO, request.pageNo.toString())
    }

    if (RequestMethod.GET == request.getMethod()) {
      if (null == request.perPage) {
        ub.setParameter(PARAM_PER_PAGE, DEFAULT_PAGE_SIZE.toString())
      } else {
        ub.setParameter(PARAM_PER_PAGE, request.perPage.toString())
      }
    }

    if (null != request.queryParams) {
      for (entry in request.queryParams!!.entries) {
        ub.setParameter(entry.key, entry.value)
      }
    }

    var uri: URI? = null
    try {
      uri = ub.build()
    } catch (use: URISyntaxException) {
      log.error(use.message, use)
    }

    return uri!!
  }

  fun createPath(request: ApiRequest): String {
    var path: String = URL_PATH_SEPARATOR + apiVersion + request.apiAction!!.path
    return if (null == request.pathParams) path else String.format(path, *request.pathParams!!)
  }

  fun createRequestData(request: ApiRequest): HttpEntity? {
    var data: StringEntity? = null

    if (null != request.data) {
      var inputData: String = serialize!!.toJson(request.data)
      try {
        data = StringEntity(inputData)
      } catch (e: UnsupportedEncodingException) {
        log.error(e.message, e)
      }
    }

    return data
  }

  fun appendRateLimitValues(response: String, httpResponse: HttpResponse): String {
    if (StringUtils.isBlank(response)) {
      return StringUtils.EMPTY
    }

    // Occasionally the DigitalOcean API will fail to send rate limit headers.
    // Simply omit rate limit data in that case.
    var rateLimit: String? = getSimpleHeaderValue(HDR_RATE_LIMIT, httpResponse)
    var rateRemaining: String? = getSimpleHeaderValue(HDR_RATE_REMAINING, httpResponse)
    var rateReset: String? = getSimpleHeaderValue(HDR_RATE_RESET, httpResponse)
    if (rateLimit == null || rateRemaining == null || rateReset == null) {
      return response
    }

    var rateLimitData: String = String.format(RATE_LIMIT_JSON_STRUCT, rateLimit, rateRemaining, getDateString(rateReset, DATE_FORMAT))
    log.debug("RateLimitData:: {}", rateLimitData)

    return StringUtils.substringBeforeLast(response, "}") + ", " + rateLimitData + "}"
  }

  fun getDateString(epochString: String, dateFormat: String): String {
    var epoch: Long = epochString.toLong()
    var expiry: Date = Date(epoch * 1000)

    var formatter: SimpleDateFormat = SimpleDateFormat(dateFormat)
    var dateString: String = formatter.format(expiry)
    log.debug(dateString)

    return dateString
  }

  /** Easy method for HTTP header values (first/last) */
  fun getSimpleHeaderValue(header: String, httpResponse: HttpResponse, first: Boolean): String? {
    if (StringUtils.isBlank(header)) {
      return StringUtils.EMPTY
    }

    var h: Header? = if (first) {
      httpResponse.getFirstHeader(header)
    } else {
      httpResponse.getLastHeader(header)
    }
    if (h == null) {
      return null
    }
    return h.value
  }

  /** Easy method for HTTP header values. defaults to first one. */
  fun getSimpleHeaderValue(header: String, httpResponse: HttpResponse): String? {
    return getSimpleHeaderValue(header, httpResponse, true)
  }

  // =======================================
  // Validation methods
  // =======================================

  fun validateDropletIdAndPageNo(dropletId: Int, pageNo: Int) {
    validateDropletId(dropletId)
    validatePageNo(pageNo)
  }

  fun validateSnapshotId(snapshotId: String) {
    checkBlankAndThrowError(snapshotId, "Missing required parameter - snapshotId.")
  }

  fun validateDropletId(dropletId: Int) {
    checkNullAndThrowError(dropletId, "Missing required parameter - dropletId.")
  }

  fun validateLoadBalancerId(loadBalancerId: String) {
    checkBlankAndThrowError(loadBalancerId, "Missing required parameter - loadBalancerId.")
  }

  fun validatePageNo(pageNo: Int) {
    checkNullAndThrowError(pageNo, "Missing required parameter - pageNo.")
  }

  fun validateProjectId(projectId: String) {
    checkNullAndThrowError(projectId, "Missing required parameter - projectId.")
  }

  fun checkNullAndThrowError(obj: Any, msg: String) {
    if (null == obj) {
      log.error(msg)
      throw IllegalArgumentException(msg)
    }
  }

  // It checks for null, whitespace and length
  fun checkBlankAndThrowError(str: String, msg: String) {
    if (StringUtils.isBlank(str)) {
      log.error(msg)
      throw IllegalArgumentException(msg)
    }
  }

  fun validateForwardingRules(rules: List<ForwardingRules>) {
    if (null == rules || rules.isEmpty()) {
      throw IllegalArgumentException("Missing required parameters [ForwardingRules]")
    }

    for (rule in rules) validateForwardingRule(rule)
  }

  fun validateForwardingRule(rule: ForwardingRules) {
    if (null == rule
        || null == rule.entryProtocol
        || null == rule.entryPort
        || null == rule.targetProtocol
        || null == rule.targetPort) {
      throw IllegalArgumentException(
          "Missing required parameters [Entry Protocol, Entry Port, Target Protocol, Target Port] for forwarding rules.")
    }
  }

  fun validateHealthCheck(healthCheck: HealthCheck) {
    if (null != healthCheck
        && (null == healthCheck.protocol || null == healthCheck.port)) {
      throw IllegalArgumentException(
          "Missing required parameters [Protocol, Port] for health check")
    }
  }

  fun initialize() {
    this.deserialize = GsonBuilder().setDateFormat(DATE_FORMAT).create()

    this.serialize =
        GsonBuilder()
            .setDateFormat(DATE_FORMAT)
            .registerTypeAdapter(Droplet::class.java, DropletSerializer())
            .registerTypeAdapter(Volume::class.java, VolumeSerializer())
            .registerTypeAdapter(LoadBalancer::class.java, LoadBalancerSerializer())
            .registerTypeAdapter(Firewall::class.java, FirewallSerializer())
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    var headers: Array<Header> = arrayOf(
      BasicHeader(HDR_USER_AGENT, USER_AGENT),
      BasicHeader(HDR_CONTENT_TYPE, JSON_CONTENT_TYPE),
      BasicHeader(HDR_AUTHORIZATION, "Bearer " + authToken)
    )
    log.debug("API Request Headers:: " + headers)

    this.requestHeaders = headers

    if (null == this.httpClient) {
      this.httpClient = HttpClients.createDefault()
    }
  }

  companion object {
      val log: Logger = LoggerFactory.getLogger(DigitalOceanClient::class.java)
  }
}
