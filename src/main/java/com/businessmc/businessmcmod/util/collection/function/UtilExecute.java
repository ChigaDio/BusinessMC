package com.businessmc.businessmcmod.util.collection.function;

import com.businessmc.businessmcmod.util.collection.JobTypeCollectionDb;

public  class UtilExecute {

    public  static  String findJobName(Integer jobId)
    {
        var result = JobTypeCollectionDb.cache_data.stream().filter(item -> item.getJobId() == jobId).findFirst();
        return result != null? result.get().getJobName() : "Bag";
    }
}
