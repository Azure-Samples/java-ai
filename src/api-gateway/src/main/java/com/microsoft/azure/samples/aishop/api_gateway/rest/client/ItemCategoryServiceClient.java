package com.microsoft.azure.samples.aishop.api_gateway.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.microsoft.azure.samples.java_ai.common.dto.ItemInfoDto;

@FeignClient(value = "item-category-service")
public interface ItemCategoryServiceClient {
    @RequestMapping(method = RequestMethod.POST, value = "/categories/ai-item-categorization")
    ItemInfoDto categorizeItem(@RequestBody final ItemInfoDto itemInfoDto);
    
}
