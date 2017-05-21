/*
 * Applitools SDK for Selenium integration.
 */
package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A base class for encapsulating data about applications to be tested.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "$type"
)

/*
 * The name's value is according to what the agent expects
 */
@JsonSubTypes({
        @Type(value = TargetWebDriverApplication.class,
                name = "Applitools.Framework.TargetWebDriverApplication, Core")
})
public class TargetApplication {

}