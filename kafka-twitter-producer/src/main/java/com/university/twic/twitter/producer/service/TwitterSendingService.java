package com.university.twic.twitter.producer.service;

import java.util.List;

public interface TwitterSendingService {
    void startSendingTweetsProcess(List<String> searchTerms);
}
