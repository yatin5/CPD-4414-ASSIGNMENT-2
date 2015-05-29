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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author YATIN PATEL
 */
public class OrderQueue {

    Queue<Order> orderQueue = new ArrayDeque<>();
    List<Order> orderList = new ArrayList<>();

    public void add(Order order) throws NoCustomerException, NoPurchasesException {
        if (order.getCustomerId().isEmpty() && order.getCustomerName().isEmpty()) {
            throw new NoCustomerException();
        }
        if (order.getListOfPurchases().isEmpty()) {
            throw new NoPurchasesException();
        }
        orderQueue.add(order);
        order.setTimeReceived(new Date());
    }

    public Order next() {
        return orderQueue.peek();
    }

    void process(Order next) throws NoTimeReceivedException {
        if (next.equals(next())) {
//            boolean isOkay = true;
//            for (Purchase p : next.getListOfPurchases()) {
//                if (Inventory.getQuantityForId(p.getProductId()) < p.getQuantity())
//                    isOkay = false;
//            }
//            if (isOkay) {
            orderList.add(orderQueue.remove());
            next.setTimeProcessed(new Date());
//            }
        } else if (next.getTimeReceived() == null) {
            throw new NoTimeReceivedException();
        }
    }

    void fulfill(Order next) throws NoTimeReceivedException, NoTimeProcessedException {
        if (next.getTimeReceived() == null) {
            throw new NoTimeReceivedException();
        }
        if (next.getTimeProcessed() == null) {
            throw new NoTimeProcessedException();
        }
        if (orderList.contains(next)) {
            next.setTimeFulfilled(new Date());
        }
    }

    String report() {
        String output = "";
        if (!(orderQueue.isEmpty() && orderList.isEmpty())) {
            JSONObject obj = new JSONObject();
            JSONArray orders = new JSONArray();
            for (Order o : orderList) {
                orders.add(o.toJSON());
            }
            for (Order o : orderQueue) {
                orders.add(o.toJSON());
            }
            obj.put("orders", orders);
            output = obj.toJSONString();
        }
        return output;
    }

    public class NoCustomerException extends Exception {

        public NoCustomerException() {
            super("No Customer Provided");
        }
    }

    public class NoPurchasesException extends Exception {

        public NoPurchasesException() {
            super("No Purchases Provided");
        }
    }

    public class NoTimeReceivedException extends Exception {

        public NoTimeReceivedException() {
            super("No Time Received on this Order");
        }
    }

    public class NoTimeProcessedException extends Exception {

        public NoTimeProcessedException() {
            super("No Time Processed on this Order");
        }
    }
}
