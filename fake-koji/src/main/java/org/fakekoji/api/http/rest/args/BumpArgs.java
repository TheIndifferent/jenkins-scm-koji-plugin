package org.fakekoji.api.http.rest.args;

import org.fakekoji.api.http.rest.OToolError;
import org.fakekoji.api.http.rest.RestUtils;
import org.fakekoji.functional.Result;
import org.fakekoji.jobmanager.model.JobCollisionAction;

import java.util.List;
import java.util.Map;

import static org.fakekoji.api.http.rest.BumperAPI.EXECUTE;
import static org.fakekoji.api.http.rest.BumperAPI.JOB_COLLISION_ACTION;

public class BumpArgs {
    public final JobCollisionAction action;
    public boolean execute;

    public BumpArgs(final JobCollisionAction action, final boolean execute) {
        this.action = action;
        this.execute = execute;
    }

    BumpArgs(final BumpArgs bumpArgs) {
        this(bumpArgs.action, bumpArgs.execute);
    }

    public static Result<BumpArgs, OToolError> parseBumpArgs(final Map<String, List<String>> paramsMap) {
        final String executeParam = RestUtils.extractParamValue(paramsMap, EXECUTE).orElse("false");
        final boolean execute = Boolean.parseBoolean(executeParam);
        final Result<JobCollisionAction, String> actionResult = RestUtils.extractParamValue(paramsMap, JOB_COLLISION_ACTION)
                .map(JobCollisionAction::parse)
                .orElse(Result.ok(JobCollisionAction.STOP));
        if (actionResult.isError()) {
            return Result.err(new OToolError(actionResult.getError(), 400));
        }
        return Result.ok(new BumpArgs(actionResult.getValue(), execute));
    }
}
