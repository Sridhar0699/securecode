package com.portal.rms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.portal.rms.entity.RmsRates;

public interface RmsRatesRepo extends CrudRepository<RmsRates, String>{

	@Query("from RmsRates rr where classifiedAdsType = ?1 and editionId in(?2) and classifiedAdsSubtype = ?3 and fixedFormat = ?4 and rr.markAsDelete = false")      
	List<RmsRates> getRates(Integer adsType, List<Integer> editions, Integer adsSubtype,Integer fixedFormat);
}
