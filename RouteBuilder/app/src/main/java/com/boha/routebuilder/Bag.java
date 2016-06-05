package com.boha.routebuilder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by aubreymalabie on 4/18/16.
 */
public class Bag {
    List<List<HashMap<String, String>>> list;
    List<TRouteDTO> routes;

    public List<List<HashMap<String, String>>> getList() {
        return list;
    }

    public void setList(List<List<HashMap<String, String>>> list) {
        this.list = list;
    }

    public List<TRouteDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<TRouteDTO> routes) {
        this.routes = routes;
    }
}
