package com.irunninglog.vertx.verticle;

import com.irunninglog.api.service.AbstractResponse;
import com.irunninglog.api.service.ResponseStatus;
import com.irunninglog.api.service.ResponseStatusException;
import com.irunninglog.vertx.Address;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

abstract class AbstractRequestResponseVerticle<Q, S extends AbstractResponse> extends AbstractVerticle {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final Supplier<S> constructor;
    private final Class<Q> requestClass;

    AbstractRequestResponseVerticle(Class<Q> requestClass, Supplier<S> responseSupplier) {
        this.constructor = responseSupplier;
        this.requestClass = requestClass;
    }

    @Override
    public final void start() throws Exception {
        logger.info("start:start");

        super.start();

        vertx.eventBus().<String>consumer(address().getAddress()).handler(handler());

        logger.info("start:end");
    }

    private Handler<Message<String>> handler() {
        return msg -> vertx.<String>executeBlocking(future -> {
                    long start = System.currentTimeMillis();

                    logger.info("handler:{}:{}", address(), msg.body());

                    try {
                        logger.info("handler:start");

                        Q request = Json.decodeValue(msg.body(), requestClass);

                        logger.info("handler:request:{}", request);

                        S response = handle(request);

                        logger.info("handler:response:{}", response);

                        future.complete(Json.encode(response));
                    } catch (Exception ex) {
                        logger.error("handler:exception:{}", ex);

                        S response = fromException(ex);

                        logger.error("handler:exception:{}", response);

                        future.complete(Json.encode(response));
                    } finally {
                        logger.info("handler:{}:{}ms", address(), System.currentTimeMillis() - start);
                    }
                },
                result -> {
                    if (result.succeeded()) {
                        msg.reply(result.result());
                    } else {
                        msg.fail(ResponseStatus.Error.getCode(), ResponseStatus.Error.getMessage());
                    }
                });
    }

    protected abstract S handle(Q request);

    protected abstract Address address();

    private S fromException(Exception ex) {
        S response = constructor.get();

        ResponseStatus status;
        boolean statusException = ex instanceof ResponseStatusException;

        logger.info("fromException:{}:{}", statusException, ex.getClass());

        if (statusException) {
            status = ((ResponseStatusException) ex).getResponseStatus();
        } else {
            status = ResponseStatus.Error;
        }

        logger.info("fromException:{}", status);

        response.setStatus(status);

        return response;
    }

}
