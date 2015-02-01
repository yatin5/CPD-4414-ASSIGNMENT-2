/*
 * Copyright 2015 Len Payne <len.payne@lambtoncollege.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cpd4414.assign2;

import cpd4414.assign2.OrderQueue;
import cpd4414.assign2.Purchase;
import cpd4414.assign2.Order;
import java.util.Date;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Len Payne <len.payne@lambtoncollege.ca>
 */
public class OrderQueueTest {

    public OrderQueueTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWhenCustomerExistsAndPurchasesExistThenTimeReceivedIsNow() throws Exception {
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("CUST00001", "ABC Construction");
        order.addPurchase(new Purchase(1, 8));
        order.addPurchase(new Purchase(2, 4));
        orderQueue.add(order);

        long expResult = new Date().getTime();
        long result = order.getTimeReceived().getTime();
        assertTrue(Math.abs(result - expResult) < 1000);
    }

    @Test
    public void testWhenNoCustomerExistsThenThrowAnException() throws OrderQueue.NoPurchasesException {
        boolean didThrow = false;
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("", "");
        order.addPurchase(new Purchase(1, 8));
        order.addPurchase(new Purchase(2, 4));
        try {
            orderQueue.add(order);
        } catch (OrderQueue.NoCustomerException ex) {
            didThrow = true;
        }

        assertTrue(didThrow);
    }

    @Test
    public void testWhenNoPurchasesThenThrowAnException() throws OrderQueue.NoCustomerException {
        boolean didThrow = false;
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeNormal", "Order");
        try {
            orderQueue.add(order);
        } catch (OrderQueue.NoPurchasesException ex) {
            didThrow = true;
        }

        assertTrue(didThrow);
    }

    @Test
    public void testGetNextWhenOrdersInSystemThenGetNextAvailable() throws OrderQueue.NoCustomerException, OrderQueue.NoPurchasesException {
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeValues", "OtherValues");
        order.addPurchase(new Purchase(1, 8));
        orderQueue.add(order);
        Order order2 = new Order("SomeValues", "OtherValues");
        order2.addPurchase(new Purchase(2, 4));
        orderQueue.add(order2);

        Order result = orderQueue.next();
        assertEquals(result, order);
        assertNull(result.getTimeProcessed());
    }

    @Test
    public void testGetNextWhenNoOrdersInSystemThenReturnNull() throws OrderQueue.NoCustomerException, OrderQueue.NoPurchasesException {
        OrderQueue orderQueue = new OrderQueue();

        Order result = orderQueue.next();
        assertNull(result);
    }

    @Test
    public void testProcessWhenTimeReceivedIsSetThenSetTimeProcessedToNow() throws OrderQueue.NoCustomerException, OrderQueue.NoPurchasesException, OrderQueue.NoTimeReceivedException {
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeValues", "OtherValues");
        order.addPurchase(new Purchase(1, 8));
        orderQueue.add(order);
        Order order2 = new Order("SomeValues", "OtherValues");
        order2.addPurchase(new Purchase(2, 4));
        orderQueue.add(order2);

        Order next = orderQueue.next();
        orderQueue.process(next);

        long expResult = new Date().getTime();
        long result = next.getTimeProcessed().getTime();
        assertTrue(Math.abs(result - expResult) < 1000);
    }

    @Test
    public void testProcessWhenTimeReceivedNotSetThenThrowException() {
        boolean didThrow = false;
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeValues", "OtherValues");
        order.addPurchase(new Purchase(1, 8));

        try {
            orderQueue.process(order);
        } catch (OrderQueue.NoTimeReceivedException ex) {
            didThrow = true;
        }

        assertTrue(didThrow);
    }

    @Test
    public void testFulfillWhenTimeReceivedIsSetAndTimeProcessedIsSetAndItemsInStockThenSetTimeFulfilledToNow() throws OrderQueue.NoCustomerException, OrderQueue.NoPurchasesException, OrderQueue.NoTimeReceivedException, OrderQueue.NoTimeProcessedException {
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeValues", "OtherValues");
        order.addPurchase(new Purchase(1, 8));
        orderQueue.add(order);
        Order order2 = new Order("SomeValues", "OtherValues");
        order2.addPurchase(new Purchase(2, 4));
        orderQueue.add(order2);

        Order next = orderQueue.next();
        orderQueue.process(next);

        orderQueue.fulfill(next);

        long expResult = new Date().getTime();
        long result = next.getTimeFulfilled().getTime();
        assertTrue(Math.abs(result - expResult) < 1000);
    }

    @Test
    public void testFulfillWhenTimeReceivedNotSetThenThrowException() throws OrderQueue.NoTimeProcessedException {
        boolean didThrow = false;
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeValues", "OtherValues");
        order.addPurchase(new Purchase(1, 8));

        try {
            orderQueue.fulfill(order);
        } catch (OrderQueue.NoTimeReceivedException ex) {
            didThrow = true;
        }

        assertTrue(didThrow);
    }

    @Test
    public void testFulfillWhenTimeProcessedNotSetThenThrowException() throws OrderQueue.NoCustomerException, OrderQueue.NoPurchasesException, OrderQueue.NoTimeReceivedException {
        boolean didThrow = false;
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("SomeValues", "OtherValues");
        order.addPurchase(new Purchase(1, 8));
        orderQueue.add(order);

        try {
            orderQueue.fulfill(order);
        } catch (OrderQueue.NoTimeProcessedException ex) {
            didThrow = true;
        }

        assertTrue(didThrow);
    }

    @Test
    public void testReportWhenNoOrdersThenReturnEmptyString() {
        OrderQueue orderQueue = new OrderQueue();
        String expResult = "";
        String result = orderQueue.report();
        assertEquals(expResult, result);
    }

    @Test
    public void testReportWhenItemsInQueueThenReturnCorrectReport() throws OrderQueue.NoCustomerException, OrderQueue.NoPurchasesException, OrderQueue.NoTimeReceivedException, OrderQueue.NoTimeProcessedException, ParseException {
        OrderQueue orderQueue = new OrderQueue();
        Order order = new Order("Cust1", "Name1");
        order.addPurchase(new Purchase(1, 8));
        orderQueue.add(order);
        Order order2 = new Order("Cust2", "Name2");
        order2.addPurchase(new Purchase(2, 4));
        orderQueue.add(order2);

        Order next = orderQueue.next();
        orderQueue.process(next);

        orderQueue.fulfill(next);

        JSONObject expResult = new JSONObject();
        JSONArray orders = new JSONArray();
        JSONObject o1 = new JSONObject();
        o1.put("customerId", "Cust1");
        o1.put("customerName", "Name1");
        o1.put("timeReceived", new Date().toString());
        o1.put("timeProcessed", new Date().toString());
        o1.put("timeFulfilled", new Date().toString());
        JSONArray pList = new JSONArray();
        JSONObject p1 = new JSONObject();
        p1.put("productId", 1);
        p1.put("quantity", 8);
        pList.add(p1);
        o1.put("purchases", pList);
        o1.put("notes", null);
        orders.add(o1);
        JSONObject o2 = new JSONObject();
        o2.put("customerId", "Cust2");
        o2.put("customerName", "Name2");
        o2.put("timeReceived", new Date().toString());
        o2.put("timeProcessed", null);
        o2.put("timeFulfilled", null);
        JSONArray pList2 = new JSONArray();
        JSONObject p2 = new JSONObject();
        p2.put("productId", 2);
        p2.put("quantity", 4);
        pList2.add(p2);
        o2.put("purchases", pList2);
        o2.put("notes", null);
        orders.add(o2);
        expResult.put("orders", orders);

        String resultString = orderQueue.report();
        System.out.println(resultString);
        JSONObject result = (JSONObject) JSONValue.parseWithException(resultString);
        System.out.println(result);
        assertEquals(expResult.toJSONString(), result.toJSONString());
    }
}