package com.irunninglog.spring.challenges;

import com.irunninglog.api.challenges.IChallenge;
import com.irunninglog.api.challenges.IChallengesService;
import com.irunninglog.api.factory.IFactory;
import com.irunninglog.api.security.IUser;
import com.irunninglog.spring.util.DistanceService;
import com.irunninglog.strava.IStravaRun;
import com.irunninglog.strava.IStravaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
final class ChallengesService implements IChallengesService {

    private final ChallengeDefinitions definitions;
    private final IFactory factory;
    private final IStravaService stravaService;
    private final DistanceService distanceService;

    @Autowired
    ChallengesService(ChallengeDefinitions definitions,
                      IFactory factory,
                      IStravaService stravaService, DistanceService distanceService) {
        super();

        this.definitions = definitions;
        this.factory = factory;
        this.stravaService = stravaService;
        this.distanceService = distanceService;
    }

    @Override
    public List<IChallenge> getChallenges(IUser user) {
        List<IStravaRun> runs = stravaService.runs(user);
        float total = 0.0F;
        for (IStravaRun run : runs) {
            total += run.getDistance();
        }

        final float done = total;
        return definitions.definitions().stream().map(definition -> factory.get(IChallenge.class)
                .setName(definition.getName())
                .setDescription(definition.getDesctiption())
                .setDistanceTotal(distanceService.mileage(definition.getDistance()))
                .setDistanceDone(distanceService.mileage(Math.min(definition.getDistance(), done)))
                .setPercentage(distanceService.percentage(definition.getDistance(), done))
                .setProgress(distanceService.progressWhereLowIsBad(distanceService.percentage(definition.getDistance(), done))))
                .collect(Collectors.toList());
    }

}