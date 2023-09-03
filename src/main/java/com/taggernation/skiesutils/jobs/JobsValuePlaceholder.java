package com.taggernation.skiesutils.jobs;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.ConfigManager;
import com.gamingmesh.jobs.container.ActionType;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobInfo;
import com.taggernation.skiesutils.SkiesUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JobsValuePlaceholder extends PlaceholderExpansion {

    private final SkiesUtils plugin;
    private final List<Job> jobList;

    public JobsValuePlaceholder(SkiesUtils plugin) {
        this.plugin = plugin;
        this.jobList = Jobs.getJobs();
    }

    /**
     * The placeholder identifier of this expansion. May not contain {@literal %},
     * {@literal {}} or _
     *
     * @return placeholder identifier that is associated with this expansion
     */
    @Override
    public @NotNull String getIdentifier() {
        return "Skiesmc";
    }

    /**
     * The author of this expansion
     *
     * @return name of the author for this expansion
     */
    @Override
    public @NotNull String getAuthor() {
        return "Edward";
    }

    /**
     * The version of this expansion
     *
     * @return current version of this expansion
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }
// %skiesmc_{jobname}_{meterial}_{action}_points%
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");
        return getValue(args[0], args[1].toUpperCase(Locale.ROOT), ActionType.valueOf(args[2].toUpperCase(Locale.ROOT)), args[3]);
    }

    private String getValue(String jobName, String material, ActionType type,String getValue) {
        for (Job job : jobList) {
            if (!job.getName().equalsIgnoreCase(jobName)) continue;
            for (JobInfo jobInfo : job.getJobInfoList().get(type)) {
                if (!jobInfo.getName().equalsIgnoreCase(material)) continue;
                return switch (getValue.toLowerCase(Locale.ROOT)) {
                    case "points" -> String.valueOf(jobInfo.getBasePoints());
                    case "exp" -> String.valueOf(jobInfo.getBaseXp());
                    case "money" -> String.valueOf(jobInfo.getBaseIncome());
                    default -> null;
                };
            }
        }
        return null;
    }
}
