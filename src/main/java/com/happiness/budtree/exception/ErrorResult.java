package com.happiness.budtree.exception;

import lombok.Builder;

@Builder
public record ErrorResult(Integer status,
                          String message
) {}
