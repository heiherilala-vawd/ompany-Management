package com.example.demo.service.utils;

public final class SpecialWarehouseUtils {

  private static final String ROUTE_WAREHOUSE_ID = "warehouse_route_id";
  private static final String AT_SELLER_WAREHOUSE_ID = "warehouse_at_seller_id";
  private static final String UNFINDABLE_WAREHOUSE_ID = "warehouse_unfindable_id";

  private SpecialWarehouseUtils() {}

  public static String routeWarehouseId() {
    return ROUTE_WAREHOUSE_ID;
  }

  public static String atSellerWarehouseId() {
    return AT_SELLER_WAREHOUSE_ID;
  }

  public static String unfindableWarehouseId() {
    return UNFINDABLE_WAREHOUSE_ID;
  }
}
