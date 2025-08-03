/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.http;

import dev.iq.test.annotation.IntegrationTest;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/** Tests the operation of the HTTP JSON client. */
@IntegrationTest
final class HttpJsonClientIntegrationTest {

    /** Test site that only returns staus codes. */
    private static final String TEST_STATUS_CODE = "https://httpbin.org/";

    /** Test site to use for RESTful calls with JSON. */
    private static final String TEST_JSON_SITE = "https://jsonplaceholder.typicode.com/";

    /** Headers to use. */
    private static final Map<String, String> HEADERS = Map.of("bearer", "bringing-gifts");

    /** Tests that status codes are properly validated. */
    @Test
    void testSimpleGet() {

        try {
            final var statusCode = HttpJsonClient.getNoReply(URI.create(TEST_STATUS_CODE + "status/200"), HEADERS);
            Assertions.assertEquals(200, statusCode);
        } catch (final HttpException e) {
            if (e.getStatusCode() == 503) {
                Assumptions.assumeTrue(false, "Service unavailable (503) - skipping test");
            }
            throw e;
        }

        try {
            HttpJsonClient.getNoReply(URI.create(TEST_STATUS_CODE + "status/500"), HEADERS);
            Assertions.fail();
        } catch (final HttpException e) {
            if (e.getStatusCode() == 503) {
                Assumptions.assumeTrue(false, "Service unavailable (503) - skipping test");
            }
            // Expected exception for 500 - test passes if we get here
        }
    }

    /** Tests issuing a GET and receiving JSON. */
    @Test
    void testSimpleJsonGet() {

        try {
            final var response =
                    HttpJsonClient.getWithReply(URI.create(TEST_JSON_SITE + "posts/1"), HEADERS, PostResponse.class);

            Assertions.assertNotNull(response);
            Assertions.assertEquals(1, response.userId);
            Assertions.assertEquals(1, response.id);
            Assertions.assertEquals(
                    "sunt aut facere repellat provident occaecati excepturi optio reprehenderit", response.title);
            Assertions.assertEquals(158, response.body.length());
        } catch (final HttpException e) {
            if (e.getStatusCode() == 503) {
                Assumptions.assumeTrue(false, "Service unavailable (503) - skipping test");
            }
            throw e;
        }
    }

    /** Tests posting and not processing a reply. */
    @Test
    void testSimpleJsonPostNoResponse() {

        try {
            final var request = new PostCreateRequest("lorem ipsum", "intentionally left blank", 13);
            final var statusCode =
                    HttpJsonClient.postJsonNoReply(URI.create(TEST_JSON_SITE + "posts"), HEADERS, request);

            Assertions.assertEquals(201, statusCode);
        } catch (final HttpException e) {
            if (e.getStatusCode() == 503) {
                Assumptions.assumeTrue(false, "Service unavailable (503) - skipping test");
            }
            throw e;
        }
    }

    /** Tests issuing a POST and receiving JSON. */
    @Test
    void testSimpleJsonPostAndResponse() {

        try {
            final var request = new PostCreateRequest("lorem ipsum", "intentionally left blank", 13);
            final var response = HttpJsonClient.postJsonWithReply(
                    URI.create(TEST_JSON_SITE + "posts"), HEADERS, request, PostResponse.class);

            Assertions.assertNotNull(response);
            Assertions.assertEquals(request.userId, response.userId);
            Assertions.assertEquals(request.title, response.title);
            Assertions.assertEquals(request.body, response.body);
            Assertions.assertTrue(response.id > 0);
        } catch (final HttpException e) {
            if (e.getStatusCode() == 503) {
                Assumptions.assumeTrue(false, "Service unavailable (503) - skipping test");
            }
            throw e;
        }
    }

    /** Post response expected to be received. */
    private record PostResponse(int userId, int id, String title, String body) {}

    /** Request to create a new post. */
    private record PostCreateRequest(String title, String body, int userId) {}
}
