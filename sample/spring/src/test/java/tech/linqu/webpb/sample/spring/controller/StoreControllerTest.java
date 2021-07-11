/*
 * Copyright (c) 2020 linqu.tech, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.linqu.webpb.sample.spring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tech.linqu.webpb.runtime.WebpbMessage;
import tech.linqu.webpb.runtime.WebpbUtils;
import tech.linqu.webpb.runtime.reactive.WebpbClient;
import tech.linqu.webpb.sample.proto.common.PageablePb;
import tech.linqu.webpb.sample.proto.store.StoreGreetingRequest;
import tech.linqu.webpb.sample.proto.store.StoreGreetingResponse;
import tech.linqu.webpb.sample.proto.store.StoreListRequest;
import tech.linqu.webpb.sample.proto.store.StoreVisitRequest;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest
class StoreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WebpbClient webpbClient;

    private MockHttpServletRequestBuilder request(WebpbMessage message) {
        return MockMvcRequestBuilders
            .request(
                HttpMethod.valueOf(message.webpbMeta().getMethod()),
                WebpbUtils.formatUrl(message)
            )
            .contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void givenStoreId_whenGetStore_thenReturnStore() throws Exception {
        int storeId = 123;
        String customer = "fakeName";
        when(webpbClient.request(any(), any()))
            .thenReturn(new StoreGreetingResponse("Welcome, " + customer));

        mvc
            .perform(request(new StoreVisitRequest((long) storeId, customer))
                .content("{\"customer\": \"" + customer + "\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.store.id", is(storeId)))
            .andExpect(jsonPath("$.store.name", is("store-" + storeId)))
            .andExpect(jsonPath("$.store.city", is("Chengdu")))
            .andExpect(jsonPath("$.greeting", is("Welcome, " + customer)));
    }

    @Test
    public void givenPageable_whenGetStores_thenReturnStoreList() throws Exception {
        mvc
            .perform(request(new StoreListRequest(new PageablePb(true, 2, 8, null))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paging.page", is(2)))
            .andExpect(jsonPath("$.stores", hasSize(8)));
    }

    @Test
    public void givenLargePageSize_whenGetStores_thenReturnBadRequest() throws Exception {
        StoreListRequest request = new StoreListRequest(new PageablePb(true, 2, 11, null));
        String url = WebpbUtils.formatUrl(request);
        assertEquals("/stores?page=2&size=11", url);

        mvc
            .perform(request(new StoreListRequest(new PageablePb(true, 2, 11, null))))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void givenNoPageable_whenGetStores_thenReturnStoreList() throws Exception {
        mvc
            .perform(request(new StoreListRequest(new PageablePb())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paging.page", is(1)))
            .andExpect(jsonPath("$.stores", hasSize(10)));
    }

    @Test
    public void givenCustomer_whenGreeting_thenReturnGreetingMessage() throws Exception {
        String customer = "fakeName";
        mvc
            .perform(request(new StoreGreetingRequest(customer))
                .content("{\"customer\": \"" + customer + "\"}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.greeting", is("Welcome, " + customer)));
    }
}
