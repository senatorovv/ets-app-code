/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.20).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package com.kumoco.productorder.api.notimplemented;

import com.kumoco.productorder.model.tmf.Error;
import com.kumoco.productorder.model.tmf.EventSubscription;
import com.kumoco.productorder.model.tmf.EventSubscriptionInput;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@javax.annotation.Generated(value = "com.kumoco.productorder.codegen.v3.generators.java.SpringCodegen", date = "2020-06-01T18:11:48.790+01:00[Europe/London]")
@Api(value = "hub", description = "the hub API")
public interface HubApi {

    @ApiOperation(value = "Register a listener", nickname = "registerListener", notes = "Sets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.", response = EventSubscription.class, tags={ "events subscription", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Subscribed", response = EventSubscription.class),
        @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method Not allowed", response = Error.class),
        @ApiResponse(code = 409, message = "Conflict", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/hub",
        produces = { "application/json;charset=utf-8" }, 
        consumes = { "application/json;charset=utf-8" },
        method = RequestMethod.POST)
    ResponseEntity<EventSubscription> registerListener(@ApiParam(value = "Data containing the callback endpoint to deliver the information" ,required=true )  @Valid @RequestBody EventSubscriptionInput body
);


    @ApiOperation(value = "Unregister a listener", nickname = "unregisterListener", notes = "Resets the communication endpoint address the service instance must use to deliver information about its health state, execution state, failures and metrics.", tags={ "events subscription", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Deleted"),
        @ApiResponse(code = 400, message = "Bad request", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "Not Found", response = Error.class),
        @ApiResponse(code = 405, message = "Method not allowed", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class) })
    @RequestMapping(value = "/hub/{id}",
        produces = { "application/json;charset=utf-8" }, 
        method = RequestMethod.DELETE)
    ResponseEntity<Void> unregisterListener(@ApiParam(value = "The id of the registered listener",required=true) @PathVariable("id") String id
);

}