package org.robolectric.shadows;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Shadow implementation for the Android {@code AccountManager } class.
 */
@Implements(AccountManager.class)
public class ShadowAccountManager {
  private static final Object lock = new Object();

  private static AccountManager instance;

  private List<Account> accounts = new ArrayList<Account>();
  private Map<Account, Map<String, String>> authTokens = new HashMap<Account, Map<String,String>>();
  private List<AuthenticatorDescription> authenticators = new ArrayList<AuthenticatorDescription>();
  private List<OnAccountsUpdateListener> listeners = new ArrayList<OnAccountsUpdateListener>();
  private Map<Account, Map<String, String>> userData = new HashMap<Account, Map<String,String>>();
  private Map<Account, String> passwords = new HashMap<Account, String>();

  public static void reset() {
    synchronized (lock) {
      instance = null;
    }
  }

  @Implementation
  public static AccountManager get(Context context) {
    synchronized (lock) {
      if (instance == null) {
        instance = Robolectric.newInstanceOf(AccountManager.class);
      }
      return instance;
    }
  }

  @Implementation
  public Account[] getAccounts() {
    return accounts.toArray(new Account[accounts.size()]);
  }

  @Implementation
  public Account[] getAccountsByType(String type) {
    List<Account> accountsByType = new ArrayList<Account>();

    for (Account a : accounts) {
      if (type.equals(a.type)) {
        accountsByType.add(a);
      }
    }

    return accountsByType.toArray(new Account[accountsByType.size()]);
  }

  @Implementation
  public synchronized void setAuthToken(Account account, String tokenType, String authToken) {
    if(accounts.contains(account)) {
      Map<String, String> tokenMap = authTokens.get(account);
      if(tokenMap == null) {
        tokenMap = new HashMap<String, String>();
        authTokens.put(account, tokenMap);
      }
      tokenMap.put(tokenType, authToken);
    }
  }

  @Implementation
  public String peekAuthToken(Account account, String tokenType) {
    Map<String, String> tokenMap = authTokens.get(account);
    if(tokenMap != null) {
      return tokenMap.get(tokenType);
    }
    return null;
  }

  @Implementation
  public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
    if (account == null) {
      throw new IllegalArgumentException("account is null");
    }
    for (Account a: getAccountsByType(account.type)) {
      if (a.name.equals(account.name)) {
        return false;
      }
    }
    
    if (!accounts.add(account)) {
    	return false;
    }
    
    setPassword(account, password);
    
    if(userdata != null) {
      for (String key : userdata.keySet()) {
        setUserData(account, key, userdata.get(key).toString());
      }
    }
    
    return true;    
  }

  @Implementation
  public String blockingGetAuthToken(Account account, String authTokenType,
                                     boolean notifyAuthFailure) {
    if (account == null) {
      throw new IllegalArgumentException("account is null");
    }
    if (authTokenType == null) {
      throw new IllegalArgumentException("authTokenType is null");
    }

    Map<String, String> tokensForAccount = authTokens.get(account);
    if (tokensForAccount == null) {
      return null;
    }
    return tokensForAccount.get(authTokenType);
  }

  @Implementation
  public AccountManagerFuture<Boolean> removeAccount (final Account account,
                                                      AccountManagerCallback<Boolean> callback,
                                                      Handler handler) {

    if (account == null) throw new IllegalArgumentException("account is null");

    final boolean accountRemoved = accounts.remove(account);
	passwords.remove(account);
	userData.remove(account);

    return new AccountManagerFuture<Boolean>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return false;
      }

      @Override
      public Boolean getResult() throws OperationCanceledException, IOException,
              AuthenticatorException {
        return accountRemoved;
      }

      @Override
      public Boolean getResult(long timeout, TimeUnit unit) throws OperationCanceledException,
              IOException, AuthenticatorException {
        return accountRemoved;
      }
    };
  }

  @Implementation
  public AuthenticatorDescription[] getAuthenticatorTypes() {
    return authenticators.toArray(new AuthenticatorDescription[authenticators.size()]);
  }

  @Implementation
  public void addOnAccountsUpdatedListener(final OnAccountsUpdateListener listener,
      Handler handler, boolean updateImmediately) {

    if (listeners.contains(listener)) {
      return;
    }

    listeners.add(listener);

    if (updateImmediately) {
      listener.onAccountsUpdated(getAccounts());
    }
  }
  
  @Implementation
  public String getUserData(Account account, String key) {
    if (account == null) {
      throw new IllegalArgumentException("account is null");
    }

    if (!userData.containsKey(account)) {
      return null;
    }
    
    Map<String, String> userDataMap = userData.get(account);
    if (userDataMap.containsKey(key)) {
      return userDataMap.get(key);
    }

	return null;
  }
  
  @Implementation
  public void setUserData(Account account, String key, String value) {
    if (account == null) {
      throw new IllegalArgumentException("account is null");
    }
    
    if (!userData.containsKey(account)) {
      userData.put(account, new HashMap<String, String>());
    }
    
    Map<String, String> userDataMap = userData.get(account);
    
    if (value == null) {
      userDataMap.remove(key);
    } else {
      userDataMap.put(key, value);
    }
  }
  
  @Implementation
  public void setPassword (Account account, String password) {
    if (account == null) {
      throw new IllegalArgumentException("account is null");
    }
    
    if (password == null) {
      passwords.remove(account);
    } else {
      passwords.put(account, password);
    }
  }
  
  @Implementation
  public String getPassword (Account account) {
    if (account == null) {
      throw new IllegalArgumentException("account is null");
    }
	
    if (passwords.containsKey(account)) {
      return passwords.get(account);
    } else {
      return null;
    }
  }

  @Implementation
  public void invalidateAuthToken(final String accountType, final String authToken) {
    Account[] accountsByType = getAccountsByType(accountType);
    for (Account account : accountsByType) {
      Map<String, String> tokenMap = authTokens.get(account);
      if (tokenMap != null) {
        Iterator<Entry<String, String>> it = tokenMap.entrySet().iterator();
        while (it.hasNext()) {
          Entry<String, String> map = it.next();
          if (map.getValue().equals(authToken)) {
            it.remove();
          }
        }
        authTokens.put(account, tokenMap);
      }
    }
  }

  private void notifyListeners() {
    Account[] accounts = getAccounts();
    Iterator<OnAccountsUpdateListener> iter = listeners.iterator();
    OnAccountsUpdateListener listener;
    while (iter.hasNext()) {
      listener = iter.next();
      listener.onAccountsUpdated(accounts);
    }
  }

  /**
   * Non-android accessor.  Allows the test case to populate the
   * list of active accounts.
   *
   * @param account
   */
  public void addAccount(Account account) {
    accounts.add(account);
    notifyListeners();
  }

  /**
   * Non-android accessor.  Allows the test case to populate the
   * list of active authenticators.
   *
   * @param authenticator
   */
  public void addAuthenticator(AuthenticatorDescription authenticator) {
    authenticators.add(authenticator);
  }

  /**
   * @see #addAuthenticator(AuthenticatorDescription)
   */
  public void addAuthenticator(String type) {
    addAuthenticator(AuthenticatorDescription.newKey(type));
  }
}
