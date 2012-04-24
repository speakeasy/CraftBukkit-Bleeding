package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class PathfinderGoalFollowParent extends PathfinderGoal {

    EntityAnimal a;
    EntityAnimal b;
    float c;
    private int d;

    public PathfinderGoalFollowParent(EntityAnimal entityanimal, float f) {
        this.a = entityanimal;
        this.c = f;
    }

    public boolean a() {
        if (this.a.getAge() >= 0) {
            return false;
        } else {
            // CraftBukkit start
            // If previous parent, see if it is still good match
            if (this.b != null) {
                if (this.b.isAlive()) {
                    double d = this.a.j(this.b);
                    if (d < 9.0D) { // Too close?  Nothing else will match
                        return false;
                    }
                    else if (d < 256.0D) {  // Not too far - stick with this parent
                        return true;
                    }
                }
            }
            this.b = null;
            // CraftBukkit end
            List list = this.a.world.a(this.a.getClass(), this.a.boundingBox.grow(8.0D, 4.0D, 8.0D));
            EntityAnimal entityanimal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while ((d0 >= 9.0D) && iterator.hasNext()) { // CraftBukkit - quit if found one too close
                Entity entity = (Entity) iterator.next();
                EntityAnimal entityanimal1 = (EntityAnimal) entity;

                if (entityanimal1.getAge() >= 0) {
                    double d1 = this.a.j(entityanimal1);

                    if (d1 <= d0) {
                        d0 = d1;
                        entityanimal = entityanimal1;
                    }
                }
            }
            if (entityanimal == null) {
                return false;
            } else if (d0 < 9.0D) {
                return false;
            } else {
                this.b = entityanimal;
                return true;
            }
        }
    }

    public boolean b() {
        if (!this.b.isAlive()) {
            return false;
        } else {
            double d0 = this.a.j(this.b);

            return d0 >= 9.0D && d0 <= 256.0D;
        }
    }

    public void c() {
        this.d = 0;
    }

    public void d() {
        // CraftBukkit start - hold on to 'parent' - save lots of work figuring out another one
        // this.b = null;
        // CraftBukkit end
    }

    public void e() {
        if (--this.d <= 0) {
            this.d = 10;
            this.a.al().a((EntityLiving) this.b, this.c);
        }
    }
}
