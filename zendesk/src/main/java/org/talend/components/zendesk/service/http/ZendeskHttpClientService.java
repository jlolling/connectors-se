package org.talend.components.zendesk.service.http;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.ListenableFuture;
import org.talend.components.zendesk.common.ZendeskDataStore;
import org.talend.components.zendesk.helpers.JsonHelper;
import org.talend.components.zendesk.service.zendeskclient.ZendeskClientService;
import org.talend.components.zendesk.sources.Reject;
import org.talend.components.zendesk.sources.get.InputIterator;
import org.talend.components.zendesk.sources.get.ZendeskGetConfiguration;
import org.talend.components.zendesk.sources.put.PagedList;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.service.Service;
import org.zendesk.client.v2.Zendesk;
import org.zendesk.client.v2.model.JobStatus;
import org.zendesk.client.v2.model.Request;
import org.zendesk.client.v2.model.Ticket;
import org.zendesk.client.v2.model.User;

import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

@Service
@Slf4j
public class ZendeskHttpClientService {

    // max allowed size for 'create items' batch in Zendesk API (see documentation)
    private final int MAX_ALLOWED_BATCH_SIZE = 100;

    @Service
    private ZendeskClientService zendeskClientService;

    @Service
    private JsonReaderFactory jsonReaderFactory;

    public User getCurrentUser(ZendeskDataStore dataStore) {
        log.debug("get current user");
        Zendesk zendeskServiceClient = zendeskClientService.getZendeskClientWrapper(dataStore).getZendeskServiceClient();
        User user = zendeskServiceClient.getCurrentUser();
        return user;
    }

    public InputIterator getRequests(ZendeskDataStore dataStore) {
        log.debug("get requests");
        Zendesk zendeskServiceClient = zendeskClientService.getZendeskClientWrapper(dataStore).getZendeskServiceClient();
        Iterable<Request> data = zendeskServiceClient.getRequests();
        return new InputIterator(data.iterator(), jsonReaderFactory);
    }

    public JsonObject putRequest(ZendeskDataStore dataStore, Request request) {
        log.debug("put requests");
        Zendesk zendeskServiceClient = zendeskClientService.getZendeskClientWrapper(dataStore).getZendeskServiceClient();
        Request newItem = zendeskServiceClient.createRequest(request);
        return JsonHelper.objectToJsonObject(newItem, jsonReaderFactory);
    }

    public InputIterator getTickets(ZendeskGetConfiguration configuration) {
        log.debug("get tickets");
        Zendesk zendeskServiceClient = zendeskClientService.getZendeskClientWrapper(configuration.getDataSet().getDataStore())
                .getZendeskServiceClient();
        Iterable<Ticket> data;
        if (configuration.isQueryStringEmpty()) {
            data = zendeskServiceClient.getTickets();
        } else {
            // the result is available on Zendesk server after indexing
            // try {
            data = zendeskServiceClient.getSearchResults(Ticket.class, configuration.getQueryString());
            // URLEncoder.encode(configuration.getQueryString(), StringHelper.STRING_CHARSET));
            // } catch (UnsupportedEncodingException e) {
            // throw new RuntimeException(e);
            // }
        }
        return new InputIterator(data.iterator(), jsonReaderFactory);
    }

    public JsonObject putTicket(ZendeskDataStore dataStore, Ticket ticket) {
        log.debug("put ticket");
        Zendesk zendeskServiceClient = zendeskClientService.getZendeskClientWrapper(dataStore).getZendeskServiceClient();
        Ticket newItem;
        if (ticket.getId() == null) {
            newItem = zendeskServiceClient.createTicket(ticket);
        } else {
            newItem = zendeskServiceClient.updateTicket(ticket);
        }
        return JsonHelper.objectToJsonObject(newItem, jsonReaderFactory);
    }

    public void putTickets(ZendeskDataStore dataStore, List<Ticket> tickets, OutputEmitter<JsonObject> success,
            OutputEmitter<Reject> reject) {
        log.debug("put tickets");
        Zendesk zendeskServiceClient = zendeskClientService.getZendeskClientWrapper(dataStore).getZendeskServiceClient();

        PagedList<Ticket> pagedList = new PagedList<>(tickets, MAX_ALLOWED_BATCH_SIZE);
        List<Ticket> listPage;
        while ((listPage = pagedList.getNextPage()) != null) {
            putTicketsPage(zendeskServiceClient, listPage, success, reject);
        }
    }

    private void putTicketsPage(Zendesk zendeskServiceClient, List<Ticket> tickets, OutputEmitter<JsonObject> success,
            OutputEmitter<Reject> reject) {
        List<Ticket> ticketsUpdate = new ArrayList<>();
        List<Ticket> ticketsCreate = new ArrayList<>();
        tickets.forEach(ticket -> {
            boolean add = ticket.getId() != null ? ticketsUpdate.add(ticket) : ticketsCreate.add(ticket);
        });

        ListenableFuture<JobStatus<Ticket>> updateFuture = ticketsUpdate.isEmpty() ? null
                : zendeskServiceClient.updateTicketsAsync(ticketsUpdate);
        ListenableFuture<JobStatus<Ticket>> createFuture = ticketsCreate.isEmpty() ? null
                : zendeskServiceClient.createTicketsAsync(ticketsCreate);

        try {
            processJobStatusFuture(updateFuture, zendeskServiceClient, success, reject, ticketsUpdate);
            processJobStatusFuture(createFuture, zendeskServiceClient, success, reject, ticketsCreate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processJobStatusFuture(ListenableFuture<JobStatus<Ticket>> future, Zendesk zendeskServiceClient,
            OutputEmitter<JsonObject> success, OutputEmitter<Reject> reject, List<Ticket> tickets)
            throws ExecutionException, InterruptedException {
        if (future == null)
            return;

        JobStatus<Ticket> jobStatus = future.get();
        while (jobStatus.getStatus() == JobStatus.JobStatusEnum.queued
                || jobStatus.getStatus() == JobStatus.JobStatusEnum.working) {
            sleep(1000);
            jobStatus = zendeskServiceClient.getJobStatus(jobStatus);
        }
        if (jobStatus.getStatus() == JobStatus.JobStatusEnum.completed) {
            jobStatus.getResults().forEach(ticket -> {
                log.info("ticket was created/updated: " + ticket.toString());
                JsonObject jsonObject = JsonHelper.objectToJsonObject(ticket, jsonReaderFactory);
                success.emit(jsonObject);
            });
        } else {
            throw new RuntimeException("Batch processing failed. " + jobStatus.getMessage() + ". Failed item: "
                    + tickets.get(jobStatus.getProgress()));
        }
    }
}