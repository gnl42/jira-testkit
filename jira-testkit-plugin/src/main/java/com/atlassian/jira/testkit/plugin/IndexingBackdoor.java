import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IndexTaskContext;
import com.atlassian.jira.issue.index.IndexException;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.issue.index.IssueIndexer;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.task.TaskDescriptor;
import com.atlassian.jira.task.TaskManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.index.IndexLifecycleManager;
import com.atlassian.jira.web.action.admin.index.IndexCommandResult;
import com.atlassian.jira.web.action.admin.index.ReIndexAsyncIndexerCommand;
import com.atlassian.jira.web.action.admin.index.ReIndexBackgroundIndexerCommand;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.log4j.Logger;
import webwork.action.ServletActionContext;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;

/**
 * Backdoor for starting/stopping/querying indexing.
 *
 * @since v5.2
 */
@AnonymousAllowed
@Consumes ({ MediaType.APPLICATION_JSON })
@Produces ({ MediaType.APPLICATION_JSON })
@Path ("/indexing")
public class IndexingBackdoor
{
    private static final Logger log = Logger.getLogger(IndexingBackdoor.class);

    private final IssueIndexer issueIndexer;
    private final TaskManager taskManager;
    private final I18nHelper.BeanFactory i18nFactory;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final IssueIndexManager issueIndexManager;

    public IndexingBackdoor(IssueIndexer issueIndexer, TaskManager taskManager, I18nHelper.BeanFactory i18nFactory, JiraAuthenticationContext jiraAuthenticationContext, IssueIndexManager issueIndexManager)
    {
        this.issueIndexer = issueIndexer;
        this.taskManager = taskManager;
        this.i18nFactory = i18nFactory;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.issueIndexManager = issueIndexManager;
    }

    @POST
    @Path ("background")
    public void triggerBackgroundIndexing()
    {
        submitIndexingTask(new ReIndexBackgroundIndexerCommand(indexManager(), log, i18n()));
    }

    @POST
    @Path ("stoptheworld")
    public void triggerStopTheWorldIndexing()
    {
        submitIndexingTask(new ReIndexAsyncIndexerCommand(getJohnsonEventContaner(), indexManager(), log, i18n()));
    }

    @POST
    @Path ("deleteIndex")
    public void deleteIndex()
    {
        issueIndexer.deleteIndexes();
    }

    @POST
    @Path("reindexAll")
    public Response reindexAll()
    {
        try
        {
            issueIndexManager.reIndexAll();
            return Response.ok().build();
        }
        catch (IndexException e)
        {
            return Response.serverError().build();
        }
    }

    @GET
    public boolean isIndexingRunning()
    {
        TaskDescriptor<IndexCommandResult> task = getIndexingTask();

        return task != null && !task.isFinished();
    }

    @GET
    @Path("consistent")
    public boolean isIndexConsistent()
    {
        return issueIndexManager.isIndexConsistent();
    }

    private TaskDescriptor<IndexCommandResult> getIndexingTask()
    {
        return taskManager.getLiveTask(new IndexTaskContext());
    }

    private TaskDescriptor<?> submitIndexingTask(Callable<?> cmd)
    {
        return taskManager.submitTask(cmd, i18n().getText("admin.indexing.jira.indexing"), new IndexTaskContext());
    }

    private IndexLifecycleManager indexManager()
    {
        return ComponentAccessor.getIssueIndexManager();
    }

    private I18nHelper i18n()
    {
        return i18nFactory.getInstance(jiraAuthenticationContext.getLoggedInUser());
    }

    private JohnsonEventContainer getJohnsonEventContaner()
    {
        ServletContext ctx = ServletActionContext.getServletContext();
        if (ctx != null)
        {
            return JohnsonEventContainer.get(ctx);
        }

        return null;
    }
}
