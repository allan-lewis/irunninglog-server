package com.irunninglog.vertx.verticle;

import com.irunninglog.profile.IProfileService;
import com.irunninglog.profile.Profile;
import com.irunninglog.profile.ProfileRequest;
import com.irunninglog.profile.ProfileResponse;
import com.irunninglog.service.ResponseStatus;
import com.irunninglog.service.ResponseStatusException;
import com.irunninglog.vertx.Address;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.TestContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;

public class ProfileVerticleTest extends AbstractVertxTest {

    private IProfileService profileService;

    @Before
    public void before(TestContext context) {
        profileService = Mockito.mock(IProfileService.class);

        ProfileVerticle profileVerticle = new ProfileVerticle(profileService);
        rule.vertx().deployVerticle(profileVerticle, context.asyncAssertSuccess());
    }

    @Test
    public void ok(TestContext context) {
        Mockito.when(profileService.get(any(ProfileRequest.class))).thenReturn(new ProfileResponse().setStatus(ResponseStatus.Ok).setBody(new Profile()));

        rule.vertx().eventBus().<String>send(Address.ProfileGet.getAddress(), Json.encode(new ProfileRequest()), context.asyncAssertSuccess(o -> {
            String s = o.body();
            ProfileResponse response = Json.decodeValue(s, ProfileResponse.class);

            context.assertEquals(ResponseStatus.Ok, response.getStatus());
            context.assertNotNull(response.getBody());
        }));
    }

    @Test
    public void statusException(TestContext context) {
        Mockito.when(profileService.get(any(ProfileRequest.class))).thenThrow(new ResponseStatusException(ResponseStatus.NotFound));

        rule.vertx().eventBus().<String>send(Address.ProfileGet.getAddress(), Json.encode(new ProfileRequest()), context.asyncAssertSuccess(o -> {
            String s = o.body();
            ProfileResponse response = Json.decodeValue(s, ProfileResponse.class);

            context.assertEquals(ResponseStatus.NotFound, response.getStatus());
            context.assertNull(response.getBody());
        }));
    }

    @Test
    public void error(TestContext context) {
        Mockito.when(profileService.get(any(ProfileRequest.class))).thenThrow(new RuntimeException());

        rule.vertx().eventBus().<String>send(Address.ProfileGet.getAddress(), Json.encode(new ProfileRequest()), context.asyncAssertSuccess(o -> {
            String s = o.body();
            ProfileResponse response = Json.decodeValue(s, ProfileResponse.class);

            context.assertEquals(ResponseStatus.Error, response.getStatus());
            context.assertNull(response.getBody());
        }));
    }

}