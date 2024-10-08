package hexlet.code.specification;


import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    public Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null ? cb.conjunction()
                : cb.like(cb.upper(root.get("name")), "%" + titleCont.toUpperCase() + "%");
    }

    public Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null ? cb.conjunction()
                : cb.equal(root.get("assignee").get("id"), assigneeId);
    }

    public Specification<Task> withStatus(String status) {
        return (root, query, cb) -> status == null ? cb.conjunction()
                : cb.equal(root.get("taskStatus").get("slug"), status);
    }

    public Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> labelId == null ? cb.conjunction()
                : cb.equal(root.get("labels").get("id"), labelId);
    }
}
