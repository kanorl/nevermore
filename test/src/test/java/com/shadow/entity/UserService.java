package com.shadow.entity;

import com.shadow.entity.cache.EntityCacheService;
import com.shadow.entity.cache.annotation.Inject;
import com.shadow.entity.lock.annotation.AutoLocked;
import com.shadow.entity.lock.annotation.LockTarget;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2014/11/27
 */
@Component
public class UserService {

    @Inject
    private EntityCacheService<Integer, User> cacheService;

    public User getUser(int id) {
        return cacheService.get(id);
    }

    @AutoLocked
    public User updateUsername(int id, @LockTarget String username) {
        User user = getUser(id);
        user.updateUsername(username);
        return user;
    }

    @AutoLocked
    public void test(@LockTarget(LockTarget.Type.Element) int a[]) {

    }


    public void removeUser(int id) {
        cacheService.remove(id);
    }

    public void addUser(int id) {
        cacheService.getOr(id, () -> User.valueOf(id));
    }
}
