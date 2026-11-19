# BookHub MCP Server Documentation

## 1. Overview

BookHub provides a Model Context Protocol (MCP) server that allows compatible AI applications and external MCP clients to interact with BookHub book catalog functionality.

The MCP server exposes tools that retrieve book information directly from the BookHub database.

The server is implemented using Spring Boot and Spring AI MCP support.

## 2. MCP Connection Details

| Property | Value |
|---|---|
| MCP endpoint | `http://localhost:8001/mcp` |
| Transport protocol | Streamable HTTP |
| Authentication | JWT Bearer Token |
| Required permission | `BOOK_READ` |
| Environment | Local development |

**Note:** The endpoint above is intended for local development. External clients on other machines cannot access it until BookHub is deployed to a reachable server.

## 3. Available MCP Tools

All four tools require the `BOOK_READ` permission.

### 3.1 get_book_count

Returns the total number of books available in the BookHub catalog.

### 3.2 get_complete_book_catalog

Retrieves the complete list of books available in the BookHub catalog.

### 3.3 get_book_details

Retrieves detailed information about a specific book using its title.

### 3.4 search_books_by_subject

Searches the BookHub catalog for potentially relevant books based on a subject.

Search results are based on available catalog information, such as category names and descriptions. They do not guarantee that a book's individual contents cover the requested subject.

## 4. Security and Authorization

The MCP endpoint requires JWT authentication.

- Clients must provide a valid JWT Bearer Token.
- The authenticated user must have the `BOOK_READ` permission.
- All four MCP tools enforce authorization using Spring Security method-level permission checks.
- Users without the required permission cannot execute these tools.

Authorization is based on permissions rather than role names.

## 5. Testing and Verification

The MCP server was tested using the MCP Inspector CLI.

The following tests were successfully completed:

| Test | Result |
|---|---|
| MCP initialization | Successful |
| MCP session establishment | Successful |
| Tool discovery (`tools/list`) | Successful |
| `get_book_count` | Successful |
| `get_complete_book_catalog` | Successful |
| `get_book_details` | Successful |
| `search_books_by_subject` | Successful |
| Authorized user (`regularuser`) | Access granted |
| Unauthorized user (`mcptestuser`) | Access denied |

### Example verification results

- `get_book_count` returned 2 books.
- `get_book_details` correctly retrieved the details of Testing Book.
- `search_books_by_subject` identified Testing Book as potentially relevant to programming.
- A user without `BOOK_READ` received an authorization error.

These tests confirmed that the MCP tools can retrieve actual BookHub database information and enforce the required permissions.

## 6. Current Limitations and Future Improvements

The current MCP implementation is running in a local development environment.

The following improvements are possible in future phases:

1. Deploy BookHub to a server accessible to external MCP clients.
2. Configure HTTPS for secure remote connections.
3. Implement an appropriate authentication flow for external clients.
4. Test integration with an external AI application such as Claude Desktop.
5. Expand the MCP tools as additional BookHub functionality becomes available.

## 7. Security Notes

- Never commit JWT tokens or passwords to source control.
- Never include the JWT secret or database credentials in this document.
- Each user should authenticate using their own account and permissions.
- Production deployment must use secure configuration and HTTPS.