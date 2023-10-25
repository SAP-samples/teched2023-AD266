package com.sap.cloud.sdk.demo.ad266.utility;

import cloudsdk.gen.namespaces.goal.GoalTask_101;
import cloudsdk.gen.namespaces.goal.Goal_101;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RestController
@RequestMapping("odata/v2/")
public class MockGoalService {
    private final Map<String, List<Goal_101>> goals = new ConcurrentHashMap<>();
    private final Map<Long, List<GoalTask_101>> tasks = new ConcurrentHashMap<>();
    private final Random rnd = new Random();

    @GetMapping("Goal_101")
    protected ResponseEntity<?> getGoals(
            @RequestParam(value = "$top", defaultValue = "100") Integer top,
            @RequestParam(value = "$filter", defaultValue = "") String filter
    ) {
        var userId = filter.replaceFirst("^userId eq \\W(.*)\\W$", "$1");
        userId = flaky(userId);
        var entries = goals.get(userId);
        return ResponseEntity.ok(
            Collections.singletonMap("d",
                Collections.singletonMap("results",
                    entries.stream().limit(top).peek(this::augmentTasks).toList())));
    }

    @GetMapping("Goal_101({id})")
    protected ResponseEntity<?> getGoal( @PathVariable String id ) {
        var numMach = Pattern.compile("\\d+(\\.[fd]\\d+)?").matcher(id);
        if(!numMach.find()) {
            throw new IllegalArgumentException("Invalid id.");
        }
        var goalId = flaky(numMach.group());
        var num = Long.valueOf(goalId);
        var goal = goals.values().stream().flatMap(List::stream).filter(g -> num.equals(g.getId())).findFirst();
        if(goal.isEmpty()) {
            throw new IllegalArgumentException("Goal not found.");
        }
        augmentTasks(goal.get());
        return ResponseEntity.ok(Collections.singletonMap("d", goal.get()));
    }

    @PostMapping("Goal_101")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected void postGoal( @RequestBody Goal_101 goal ) {
        var userId = goal.getUserId();
        if(userId==null) {
            throw new IllegalArgumentException("Missing userId.");
        }
        userId = flaky(userId);
        var goals = this.goals.computeIfAbsent(userId, id -> new ArrayList<>());
        var baseId = Math.abs(userId.hashCode()%1000000L);
        goal.setId(baseId+goals.size());
        goals.add(goal);
    }

    @DeleteMapping("Goal_101({id})")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected void deleteGoal( @PathVariable String id ) {
        var numMach = Pattern.compile("\\d+(\\.[fd]\\d+)?").matcher(id);
        if(!numMach.find()) {
            throw new IllegalArgumentException("Invalid id.");
        }
        var goalId = flaky(numMach.group());
        var num = Long.valueOf(goalId);
        goals.values().forEach(list -> list.removeIf(goal -> num.equals(goal.getId())));
    }

    @PostMapping("GoalTask_101")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected void postTask( @RequestBody GoalTask_101 task ) {
        var goalId = task.getObjId();
        if(goalId==null) {
            throw new IllegalArgumentException("Missing objId.");
        }
        var tasks = this.tasks.computeIfAbsent(goalId, id -> new ArrayList<>());
        var baseId = Math.abs(goalId.hashCode()%1000000L);
        task.setId(baseId+tasks.size());
        tasks.add(task);
    }

    @DeleteMapping("GoalTask_101({id})")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    protected void deleteTask( @PathVariable String id ) {
        var numMach = Pattern.compile("\\d+(\\.[fd]\\d+)?").matcher(id);
        if(!numMach.find()) {
            throw new IllegalArgumentException("Invalid id.");
        }
        var goalId = flaky(numMach.group());
        var num = Long.valueOf(goalId);
        tasks.values().forEach(list -> list.removeIf(t -> num.equals(t.getId())));
    }

    private void augmentTasks(Goal_101 g) {
        g.setCustomField("tasks", Collections.singletonMap("results", tasks.get(g.getId())));
    }

    private String flaky(String id) {
        var flakyMatch = Pattern.compile("\\.([df])(\\d+)$").matcher(id);
        if(!flakyMatch.find()) {
            return id;
        }
        var num = Integer.parseInt(flakyMatch.group(2));
        switch (flakyMatch.group(1)) {
            case "d":
                try { Thread.sleep(num); } catch (InterruptedException e) { /* delay interrupted */ }
                break;
            case "f":
                if(rnd.nextInt(100)< num) { throw new RuntimeException("Flaky service."); }
                break;
        }
        return id.substring(0, flakyMatch.start());
    }
}
