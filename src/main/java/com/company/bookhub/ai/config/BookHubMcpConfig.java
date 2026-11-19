
package com.company.bookhub.ai.config;

import com.company.bookhub.ai.service.BookCatalogTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookHubMcpConfig {

    @Bean
    public ToolCallbackProvider bookHubMcpTools(
            BookCatalogTools bookCatalogTools
    ) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(bookCatalogTools)
                .build();
    }
}




