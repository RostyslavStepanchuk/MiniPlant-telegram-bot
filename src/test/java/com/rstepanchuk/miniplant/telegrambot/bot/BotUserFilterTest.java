package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BotUserFilterTest {

  @InjectMocks
  private BotUserFilter botUserFilter;
  @Mock
  private UserRepository userRepository;

  @Test
  void nullParamShouldThrowNullPointer() {
    assertThrows(NullPointerException.class, ()-> botUserFilter.processUpdate(null));
  }

  @Test
  void dummyTest() {
    boolean test = true;
    assertTrue(test);
  }
}