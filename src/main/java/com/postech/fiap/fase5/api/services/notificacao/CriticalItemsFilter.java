package com.postech.fiap.fase5.api.services.notificacao;

import java.util.List;

public interface CriticalItemsFilter<T> {

    List<T> filterCriticalItems(List<T> items);
}

