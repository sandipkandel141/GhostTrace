package com.ghosttrace.demo.inventoryservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    public record StockResponse(String sku, int requestedQuantity, boolean available) {}

    @GetMapping("/{sku}")
    public StockResponse check(@PathVariable String sku, @RequestParam int quantity) {
        if (sku.isBlank() || quantity < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU and positive quantity required");
        // Demo rule only: TEST-OUT is unavailable; all other SKUs have stock.
        return new StockResponse(sku, quantity, !sku.equalsIgnoreCase("TEST-OUT"));
    }
}
