package com.outsourcing.domain.menu.dto.response;

import com.outsourcing.domain.menu.entity.Menu;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MenuResponse {

    private final Long id;
    private final String name;
    private final int price;
    private final String description;
    private final String imageUrl;

    public static MenuResponse of(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getPrice(),
                menu.getDescription(),
                menu.getImageUrl()
        );
    }
}
