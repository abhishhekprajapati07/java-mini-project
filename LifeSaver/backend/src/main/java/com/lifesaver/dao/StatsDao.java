package com.lifesaver.dao;

import com.lifesaver.dto.StatsDto;

/**
 * StatsDao Interface
 * Abstracts calculation of system-wide statistics from real database tables.
 */
public interface StatsDao {

    StatsDto getStats();
}
