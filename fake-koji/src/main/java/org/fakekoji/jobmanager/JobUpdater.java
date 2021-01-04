package org.fakekoji.jobmanager;

import org.fakekoji.functional.Tuple;
import org.fakekoji.jobmanager.model.Job;
import org.fakekoji.jobmanager.model.JobBump;
import org.fakekoji.jobmanager.model.JobCollisionAction;
import org.fakekoji.jobmanager.model.JobUpdateResults;
import org.fakekoji.jobmanager.model.Project;
import org.fakekoji.model.Platform;
import org.fakekoji.model.Task;
import org.fakekoji.storage.StorageException;

import java.util.Set;
import java.util.function.Function;

public interface JobUpdater {

    JobUpdateResults update(Project oldProject, Project newProject) throws StorageException, ManagementException;

    JobUpdateResults regenerate(Project project, String whitelist) throws StorageException, ManagementException;

    <T extends Project> JobUpdateResults regenerateAll(
            String projectId,
            Manager<T> projectManager,
            String whitelist
    ) throws StorageException, ManagementException;

    JobUpdateResults update(Platform platform) throws StorageException;

    JobUpdateResults update(Task task) throws StorageException;

    Function<Tuple<Job, Job>, JobBump> getCollisionCheck();

    Set<Tuple<Job, Job>> findCollisions(final Set<Tuple<Job, Job>> jobTuples);

    JobUpdateResults bump(final Set<JobBump> jobTuple, final JobCollisionAction action);
}
