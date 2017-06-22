package com.irunninglog.vertx;

import com.irunninglog.api.IRequest;
import com.irunninglog.api.IResponse;
import com.irunninglog.api.ResponseStatus;
import com.irunninglog.api.factory.IFactory;
import com.irunninglog.api.mapping.IMapper;
import com.irunninglog.api.security.AuthnException;
import com.irunninglog.api.security.IAuthenticationService;
import com.irunninglog.api.security.IUser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.function.Function;

import static org.mockito.Matchers.any;

@RunWith(VertxUnitRunner.class)
public abstract class AbstractTest {

    private static final int PORT = 8889;

    private final IAuthenticationService authenticationService = Mockito.mock(IAuthenticationService.class);
    private final IFactory factory = Mockito.mock(IFactory.class);
    private final IMapper mapper = Mockito.mock(IMapper.class);

    private Vertx vertx;

    @Before
    public final void before(TestContext context) throws Exception {
        vertx = Vertx.vertx();

        Mockito.when(mapper.encode(null)).thenReturn("");

        IRequest request = Mockito.mock(IRequest.class);
        Mockito.when(request.getMap()).thenReturn(new HashMap<>());
        Mockito.when(factory.get(IRequest.class)).thenReturn(request);
        Mockito.when(mapper.decode(null, IRequest.class)).thenReturn(request);

        Mockito.when(factory.get(IResponse.class)).thenReturn(Mockito.mock(IResponse.class));

        Async async = context.async();

        ServerVerticle verticle = new ServerVerticle(PORT, context.asyncAssertSuccess(event -> async.complete()), factory, mapper, authenticationService);
        vertx.deployVerticle(verticle, context.asyncAssertSuccess());

        async.awaitSuccess(5000);

        afterBefore(context);
    }

    protected void afterBefore(TestContext context) {

    }

    protected void deploy(AbstractVerticle verticle, TestContext context) {
        vertx.deployVerticle(verticle, context.asyncAssertSuccess());
    }

    @After
    public final void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    protected final int get(TestContext context, String path) {
        HttpClient client = vertx.createHttpClient();

        return makeRequest(null, client::get, path, context);
    }

    int put(TestContext context, String path, String body) {
        HttpClient client = vertx.createHttpClient();

        return makeRequest(body, client::put, path, context);
    }

    private int makeRequest(String body, Function<RequestOptions, HttpClientRequest> function, String path, TestContext context) {
        Async async = context.async();

        RequestOptions options = new RequestOptions();
        options.setHost("localhost");
        options.setPort(PORT);
        options.setSsl(Boolean.FALSE);
        options.setURI(path);

        final int[] responseCode = new int[1];
        function.apply(options).handler(resp -> {
            responseCode[0] = resp.statusCode();
            async.complete();
        }).end(body == null ? "" : body);

        async.awaitSuccess(10000);

        return responseCode[0];
    }

    protected IMapper mapper() {
        return mapper;
    }

    protected IFactory factory() {
        return factory;
    }

    protected IAuthenticationService authenticationService() {
        return authenticationService;
    }

    protected void throwOnAuthentication(AuthnException exception) throws AuthnException {
        Mockito.when(authenticationService.authenticateToken(any(String.class))).thenThrow(exception);
    }

    protected void returnFromAuthentication(IUser user) throws AuthnException {
        Mockito.when(authenticationService.authenticateToken(any(String.class))).thenReturn(user);
    }

    protected void setResponseCode(ResponseStatus responseStatus) {
        IResponse mockResponse = Mockito.mock(IResponse.class);
        Mockito.when(mockResponse.setStatus(any(ResponseStatus.class))).thenReturn(mockResponse);
        Mockito.when(mapper.decode(null, IResponse.class)).thenReturn(mockResponse);
        Mockito.when(mockResponse.getStatus()).thenReturn(responseStatus);

        Mockito.when(factory.get(IResponse.class)).thenReturn(mockResponse);
    }

}