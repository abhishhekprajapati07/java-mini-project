package com.lifesaver.service;

import com.lifesaver.dao.StatsDao;
import com.lifesaver.dto.StatsDto;
import org.springframework.stereotype.Service;

/**
 * StatsServiceImpl
 * Delegates aggregated queries to StatsDao.
 */
@Service
public class StatsServiceImpl implements StatsService {

    private final StatsDao statsDao;

    public StatsServiceImpl(StatsDao statsDao) {
        this.statsDao = statsDao;
    }

    @Override
    public StatsDto getStats() {
        return statsDao.getStats();
    }
}
