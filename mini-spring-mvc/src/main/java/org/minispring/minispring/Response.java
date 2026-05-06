package org.minispring.minispring;

import java.io.PrintWriter;

/**
 * ============================================================
 * CLASS: Response
 * ============================================================
 *
 * This class represents the HTTP RESPONSE that we send back to the client
 * (browser, Postman, curl, etc.) after processing their request.
 *
 * It wraps a low-level PrintWriter (the raw socket output stream) and
 * provides a simple write() method that formats the output as a proper
 * HTTP response automatically.
 *
 * WHAT AN HTTP RESPONSE LOOKS LIKE (raw text sent back over the socket):
 * ┌─────────────────────────────────────────────────────────────────┐
 * │ HTTP/1.1 200 OK                                                 │ ← status line
 * │ Content-Type: text/html                                         │ ← header
 * │                                                                 │ ← blank line
 * │ [your body content here]                                        │ ← body
 * └─────────────────────────────────────────────────────────────────┘
 *
 * Our write() method builds this structure for every response so that
 * controller methods can just say:  response.write("Hello!");
 * without worrying about the HTTP protocol details.
 *
 * It is similar to Spring's HttpServletResponse.
 * ============================================================
 */
public class Response {

    // The underlying writer connected to the client socket's output stream.
    // Every call to println() / print() on this writer sends bytes to the client.
    private PrintWriter writer;

    /**
     * Constructor — stores the writer so write() can use it later.
     *
     * @param writer a PrintWriter connected to the client socket's OutputStream
     */
    public Response(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Sends a text response back to the HTTP client.
     *
     * This method writes a complete, valid HTTP/1.1 response:
     *   - Status line:  HTTP/1.1 200 OK
     *   - Header:       Content-Type: text/html
     *   - Blank line:   (required by the HTTP spec to separate headers from body)
     *   - Body:         the string you pass in
     *
     * After writing, it calls flush() to make sure all data is actually
     * sent over the network immediately (not left sitting in a buffer).
     *
     * @param body the text content to send as the response body
     *             (can be plain text, HTML, or a JSON string)
     */
    public void write(String body) {
        writer.println("HTTP/1.1 200 OK");          // status: success
        writer.println("Content-Type: text/html");  // tell client what format the body is in
        writer.println();                            // blank line — required by HTTP spec
        writer.print(body);                          // the actual content
        writer.flush();                              // push all buffered data to the network
    }
}
