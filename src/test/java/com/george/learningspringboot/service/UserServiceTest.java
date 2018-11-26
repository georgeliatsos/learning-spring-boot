package com.george.learningspringboot.service;

import com.george.learningspringboot.dao.FakeDataDao;
import com.george.learningspringboot.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


public class UserServiceTest {

    @Mock
    private FakeDataDao fakeDataDao;

    private UserService userService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(fakeDataDao);
    }

    @Test
    public void shouldGetAllUsers() {
        UUID annaUserUid = UUID.randomUUID();

        User anna = new User(annaUserUid, "anna",
                "montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        List<User> users  = Arrays.asList(anna);

        given(fakeDataDao.selectAllUsers()).willReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertThat(allUsers).hasSize(1);

        User user = allUsers.get(0);

        assertUserFields(user);
    }

    @Test
    public void shouldGetUser() throws Exception {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna",
                "montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));

        Optional<User> userOptional = userService.getUser(annaUid);

        assertThat(userOptional.isPresent()).isTrue();
        User user = userOptional.get();

        assertUserFields(user);
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna",
                "montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));
        given(fakeDataDao.updateUser(anna)).willReturn(1);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        int updateResult = userService.updateUser(anna);

        verify(fakeDataDao).selectUserByUserUid(annaUid);
        verify(fakeDataDao).updateUser(captor.capture());

        User user = captor.getValue();
        assertUserFields(user);

        assertThat(updateResult).isEqualTo(1);
    }

    @Test
    public void shouldRemoveUser() throws Exception {
        UUID annaUid = UUID.randomUUID();
        User anna = new User(annaUid, "anna",
                "montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.selectUserByUserUid(annaUid)).willReturn(Optional.of(anna));
        given(fakeDataDao.deleteUserByUserUid(annaUid)).willReturn(1);


        int deleteResult = userService.removeUser(annaUid);

        verify(fakeDataDao).selectUserByUserUid(annaUid);
        verify(fakeDataDao).deleteUserByUserUid(annaUid);

        assertThat(deleteResult).isEqualTo(1);
    }

    @Test
    public void shouldInsertUser() throws Exception {
        User anna = new User(null, "anna",
                "montana", User.Gender.FEMALE, 30, "anna@gmail.com");

        given(fakeDataDao.insertUser(any(UUID.class), eq(anna))).willReturn(1);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        int insertResult = userService.insertUser(anna);

        verify(fakeDataDao).insertUser(any(UUID.class), captor.capture());

        User user = captor.getValue();

        assertUserFields(user);

        assertThat(insertResult).isEqualTo(1);
    }

    private void assertUserFields(User user) {
        assertThat(user.getAge()).isEqualTo(30);
        assertThat(user.getFirstName()).isEqualTo("anna");
        assertThat(user.getLastName()).isEqualTo("montana");
        assertThat(user.getGender()).isEqualTo(User.Gender.FEMALE);
        assertThat(user.getEmail()).isEqualTo("anna@gmail.com");
        assertThat(user.getUserUid()).isNotNull();
        assertThat(user.getUserUid()).isInstanceOf(UUID.class);
    }
}