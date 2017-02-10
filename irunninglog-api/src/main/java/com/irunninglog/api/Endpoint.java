package com.irunninglog.api;

import com.google.common.base.MoreObjects;

public enum Endpoint {

    Login("407a8fc3-ce60-4076-81d6-a7432dd7defb",
            "/authn",
            AccessControl.AllowAll,
            EndpointMethod.POST),
    GetDashboard("c83068b3-2d60-4578-b981-a6c173441ba4",
            "/profiles/:profileid/dashboard",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetMileageByMonth("3faf5e22-be23-4148-b900-f11abc327b98",
            "/profiles/:profileid/reports/mileagebymonth",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetMileageByRoute("b29317e2-68a0-4512-8c21-98d5e7fdd937",
            "/profiles/:profileid/reports/mileagebyroute",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetMileageByRun("e6cadd0a-bce9-437d-a81f-d63f27415777",
            "/profiles/:profileid/reports/mileagebyrun",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetMileageByShoe("1e099ab5-4d70-4407-bd6b-931b2e7f3081",
            "/profiles/:profileid/reports/mileagebyshoe",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetProfile("fff11faa-bf42-442c-ac31-eb338c7cf81e",
            "/profiles/:profileid",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetRoutes("cd36eb96-9552-4590-a33b-f67f41cfe64c",
            "/profiles/:profileid/routes",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetRuns("05c28b1c-030f-4d6d-abe8-3d7d65daf665",
            "/profiles/:profileid/runs",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetShoes("1f1b6a8f-07a5-46a3-a481-5ab2e01be51e",
            "/profiles/:profileid/shoes",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    GetWorkouts("8f639fb2-f4ac-431e-83cb-25c957d0c835",
            "/profiles/:profileid/workouts*",
            AccessControl.AllowProfile,
            EndpointMethod.GET),
    Ping("6678d445-030f-4aad-b360-6304588c07b6",
            "/ping",
            AccessControl.AllowAnonymous,
            EndpointMethod.GET),
    Forbidden("a797bebb-80e7-4739-ac38-9154f4de740c",
            "/forbidden",
            AccessControl.DenyAll,
            EndpointMethod.GET);

    private final String address;
    private final String path;
    private final AccessControl control;
    private final EndpointMethod method;

    Endpoint(String address, String path, AccessControl control, EndpointMethod method) {
        this.address = address;
        this.path = path;
        this.control = control;
        this.method = method;
    }

    public String getAddress() {
        return address;
    }

    public String getPath() {
        return path;
    }

    public AccessControl getControl() {
        return control;
    }

    public EndpointMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("path", path)
                .add("control", control)
                .add("method", method)
                .toString();
    }

}