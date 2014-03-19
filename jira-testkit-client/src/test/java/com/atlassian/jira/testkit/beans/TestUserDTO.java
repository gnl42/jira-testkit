package com.atlassian.jira.testkit.beans;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.ApplicationUser;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Unit test of this DTO.
 *
 * @since 6.3.6
 */
public class TestUserDTO
{
    private static final boolean ACTIVE = true;
    private static final long DIRECTORY_ID = 1L;
    private static final String DISPLAY_NAME = "a";
    private static final String EMAIL = "b";
    private static final String KEY = "c";
    private static final String NAME = "d";
    private static final String USERNAME = "e";

    @Test
    public void gettingAsApplicationUserShouldUseOwnValuesForBothUserTypes()
    {
        // Set up
        final UserDTO user = new UserDTO(ACTIVE, DIRECTORY_ID, DISPLAY_NAME, EMAIL, KEY, NAME, USERNAME);
        final User mockDirectoryUser = mock(User.class);

        // Invoke
        final ApplicationUser applicationUser = user.asApplicationUser(mockDirectoryUser);

        // Check
        assertTestValues(applicationUser);
        assertTestValues(applicationUser.getDirectoryUser());
    }

    private void assertTestValues(final ApplicationUser applicationUser)
    {
        assertThat(applicationUser.getDirectoryId(), is(DIRECTORY_ID));
        assertThat(applicationUser.getDisplayName(), is(DISPLAY_NAME));
        assertThat(applicationUser.getEmailAddress(), is(EMAIL));
        assertThat(applicationUser.getKey(), is(KEY));
        assertThat(applicationUser.getName(), is(NAME));
        assertThat(applicationUser.getUsername(), is(USERNAME));
        assertThat(applicationUser.isActive(), is(ACTIVE));
    }

    private void assertTestValues(final User directoryUser)
    {
        assertThat(directoryUser.getDirectoryId(), is(DIRECTORY_ID));
        assertThat(directoryUser.getDisplayName(), is(DISPLAY_NAME));
        assertThat(directoryUser.getEmailAddress(), is(EMAIL));
        assertThat(directoryUser.getName(), is(NAME));
        assertThat(directoryUser.isActive(), is(ACTIVE));
    }
}
