package teamroots.roots.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import teamroots.roots.Roots;
import teamroots.roots.effect.EffectArcanism;
import teamroots.roots.effect.EffectFireResist;
import teamroots.roots.effect.EffectManager;
import teamroots.roots.effect.EffectNaturesCure;
import teamroots.roots.effect.EffectRegen;
import teamroots.roots.particle.ParticleUtil;

public class EntityFairy extends EntityFlying {
	public static final DataParameter<Integer> variant = EntityDataManager.<Integer>createKey(EntityFairy.class, DataSerializers.VARINT);
	public static final DataParameter<BlockPos> spawnPosition = EntityDataManager.<BlockPos>createKey(EntityFairy.class, DataSerializers.BLOCK_POS);
	public static final DataParameter<BlockPos> targetPosition = EntityDataManager.<BlockPos>createKey(EntityFairy.class, DataSerializers.BLOCK_POS);
	public static final DataParameter<Boolean> tame = EntityDataManager.<Boolean>createKey(EntityFairy.class, DataSerializers.BOOLEAN);
	public static final DataParameter<Boolean> sitting = EntityDataManager.<Boolean>createKey(EntityFairy.class, DataSerializers.BOOLEAN);
	public static UUID owner = null;
	public EntityFairy(World world){
		super(world);
		setSize(0.45f,0.6f);
		this.experienceValue = 10;
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand){
		if (player.getHeldItem(hand).isEmpty()){
			getDataManager().set(sitting, !getDataManager().get(sitting));
			getDataManager().setDirty(sitting);
			return true;
		}
		return false;
	}
	
	public enum FairyType {
		GREEN, PURPLE, PINK, ORANGE
	}
	
	public float getRed(){
		switch (getDataManager().get(variant)){
		case 0: {return 177;}
		case 1: {return 219;}
		case 2: {return 255;}
		case 3: {return 255;}
		}
		return 0;
	}
	
	public float getGreen(){
		switch (getDataManager().get(variant)){
		case 0: {return 255;}
		case 1: {return 179;}
		case 2: {return 163;}
		case 3: {return 223;}
		}
		return 0;
	}
	
	public float getBlue(){
		switch (getDataManager().get(variant)){
		case 0: {return 117;}
		case 1: {return 255;}
		case 2: {return 255;}
		case 3: {return 163;}
		}
		return 0;
	}
	
    public boolean canBePushed()
    {
        return false;
    }

    protected void collideWithEntity(Entity entityIn)
    {
    }

    protected void collideWithNearbyEntities()
    {
    }
    
    public void onUpdate(){
    	super.onUpdate();
    	if (world.isRemote){
    		for (int i = 0; i < 2; i ++){
		    	float x = (float)posX + 0.25f*(rand.nextFloat()-0.5f);
		    	float y = (float)posY + 0.375f + 0.25f*(rand.nextFloat()-0.5f);
		    	float z = (float)posZ + 0.25f*(rand.nextFloat()-0.5f);
		    	ParticleUtil.spawnParticleGlow(world, x, y, z, 0.0375f*(rand.nextFloat()-0.5f), 0.0375f*(rand.nextFloat()-0.5f), 0.0375f*(rand.nextFloat()-0.5f), getRed(), getGreen(), getBlue(), 0.125f, 6.0f+6.0f*rand.nextFloat(), 20);
    		}
    	}
    }

    protected void updateAITasks()
    {
        super.updateAITasks();
        
        if (getDataManager().get(tame) && owner != null){
        	this.noClip = true;
        	EntityPlayer p = world.getPlayerEntityByUUID(owner);
        	if (getDataManager().get(sitting)){
        		motionX *= 0.9;
        		motionY *= 0.9;
        		motionZ *= 0.9;
        		if (p != null){
        			this.faceEntity(p, 30f, 30f);
        		}
        	}
        	else if (p != null){
        		double targX = p.posX;
        		double targY = p.posY + p.height;
        		double targZ = p.posZ;
        		int count = 1;
        		if (this.getDistanceSqToEntity(p) < 16.0f){
        			List<EntityFairy> list = world.getEntitiesWithinAABB(EntityFairy.class, p.getEntityBoundingBox().expandXyz(4.0));
        			List<EntityFairy> prunedList = new ArrayList<EntityFairy>();
        			for (EntityFairy f : list){
        				if (f.getDataManager().get(f.tame) && f.owner != null && f.owner.compareTo(p.getUniqueID()) == 0){
        					prunedList.add(f);
        				}
        			}
        			for (int i = 0; i < prunedList.size(); i ++){
        				if (prunedList.get(i).getUniqueID().compareTo(getUniqueID()) == 0){
        					float coeff = (float)i/(float)(prunedList.size());
        					if (prunedList.size() > 1){
        						coeff = (float)i/(float)(prunedList.size()-1f);
        					}
        			        targX = (double)p.posX + (p.width*1.5)*Math.sin(Math.toRadians((-p.rotationYaw-90.0)-180.0*coeff));
        			        targY = (double)p.posY+p.height;
        			        targZ = (double)p.posZ + (p.width*1.5)*Math.cos(Math.toRadians((-p.rotationYaw-90.0)-180.0*coeff));
        				}
        				else {
        					if (prunedList.get(i).getDataManager().get(variant) == getDataManager().get(variant)){
        						count ++;
        					}
        				}
        			}
        		}
        		
        		switch(getDataManager().get(variant)){
	        		case 0:{
	        			if (EffectManager.getDuration(p, EffectManager.effect_naturescure.name) < 2){
	        				EffectManager.assignEffect(p, EffectManager.effect_naturescure.name, 22, EffectNaturesCure.createData(count));
	        			}
	        			break;
	        		}
	        		case 1:{
	        			if (EffectManager.getDuration(p, EffectManager.effect_arcanism.name) < 2){
	        				EffectManager.assignEffect(p, EffectManager.effect_arcanism.name, 22, EffectArcanism.createData(count));
	        			}
	        			break;
	        		}
	        		case 2:{
	        			if (EffectManager.getDuration(p, EffectManager.effect_regen.name) < 2){
	        				EffectManager.assignEffect(p, EffectManager.effect_regen.name, 22, EffectRegen.createData(count));
	        			}
	        			break;
	        		}
	        		case 3:{
	        			if (EffectManager.getDuration(p, EffectManager.effect_fireresist.name) < 2){
	        				EffectManager.assignEffect(p, EffectManager.effect_fireresist.name, 22, EffectFireResist.createData(count));
	        			}
	        			break;
	        		}
        		}
        		
        		
		        double dX = targX - this.posX;
		        double dY = targY - this.posY;
		        double dZ = targZ - this.posZ;
		        double c = p.isSneaking() ? 0.3 : 1.0;
		        this.motionX += (Math.signum(dX) * 1.4f * c - this.motionX) * 0.025D;
		        this.motionY += (Math.signum(dY) * 2.2f * c - this.motionY) * 0.025D;
		        this.motionZ += (Math.signum(dZ) * 1.4f * c - this.motionZ) * 0.025D;
		        //System.out.println(motionX+", "+motionY+", "+motionZ);
		        float f = (float)(MathHelper.atan2(this.motionZ, this.motionX) * (180D / Math.PI)) - 90.0F;
		        float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
		        this.moveForward = 0.5F;
		        this.rotationYaw += f1;
        	}
        	else if (p == null){
        		motionX *= 0.9;
        		motionY *= 0.9;
        		motionZ *= 0.9;
        	}
        }
        else {
        	this.noClip = false;
        	if (this.getDataManager().get(spawnPosition).getY() < 0){
	        	this.getDataManager().set(spawnPosition, getPosition());
	        	getDataManager().setDirty(spawnPosition);
	        	this.getDataManager().set(targetPosition, getPosition());
	        	getDataManager().setDirty(targetPosition);
	        }
	
	        if (getDataManager().get(targetPosition).compareTo(getDataManager().get(spawnPosition)) == 0 || this.rand.nextInt(30) == 0 || getDataManager().get(targetPosition).distanceSq((double)((int)this.posX), (double)((int)this.posY), (double)((int)this.posZ)) < 3.0D)
	        {
	        	this.getDataManager().set(targetPosition, new BlockPos(getDataManager().get(spawnPosition).getX() + this.rand.nextInt(15) - this.rand.nextInt(15), getDataManager().get(spawnPosition).getY() + this.rand.nextInt(11) - 2, getDataManager().get(spawnPosition).getZ() + this.rand.nextInt(15) - this.rand.nextInt(15)));
	        	getDataManager().setDirty(targetPosition);
	        }
	        
	        BlockPos blockpos = new BlockPos(this);
	        BlockPos blockpos1 = blockpos.up();
	        double dX = (double)this.getDataManager().get(targetPosition).getX() + 0.5D - this.posX;
	        double dY = (double)this.getDataManager().get(targetPosition).getY() + 0.1D - this.posY;
	        double dZ = (double)this.getDataManager().get(targetPosition).getZ() + 0.5D - this.posZ;
	        this.motionX += (Math.signum(dX) * 0.5D - this.motionX) * 0.025D;
	        this.motionY += (Math.signum(dY) * 0.7D - this.motionY) * 0.025D;
	        this.motionZ += (Math.signum(dZ) * 0.5D - this.motionZ) * 0.025D;
	        float f = (float)(MathHelper.atan2(this.motionZ, this.motionX) * (180D / Math.PI)) - 90.0F;
	        float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
	        this.moveForward = 0.5F;
	        this.rotationYaw += f1;
	    }
    }
        

    public boolean doesEntityNotTriggerPressurePlate()
    {
        return true;
    }
    
    protected boolean canTriggerWalking()
    {
        return false;
    }

    public void fall(float distance, float damageMultiplier)
    {
    }

    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
    }
	
	@Override
	protected void entityInit(){
		super.entityInit();
		this.getDataManager().register(tame, false);
		this.getDataManager().register(sitting, false);
		this.getDataManager().register(variant, rand.nextInt(4));
		this.getDataManager().register(spawnPosition, new BlockPos(0,-1,0));
		this.getDataManager().register(targetPosition, new BlockPos(0,-1,0));
	}

    protected void initEntityAI()
    {
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }

	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.0D);
    }
	
	@Override
	public ResourceLocation getLootTable(){
		return new ResourceLocation("roots:entity/fairy");
	}

    public float getEyeHeight()
    {
        return this.height;
    }
    
    @Override
    public int getBrightnessForRender(float partialTicks){
    	return 255;
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey("owner")){
			owner = NBTUtil.getUUIDFromTag(compound.getCompoundTag("owner"));
		}
		
		getDataManager().set(tame, compound.getBoolean("tame"));
		getDataManager().setDirty(tame);
		getDataManager().set(sitting, compound.getBoolean("sitting"));
		getDataManager().setDirty(sitting);
		
		getDataManager().set(variant, compound.getInteger("variant"));
		getDataManager().setDirty(variant);
		
		getDataManager().set(spawnPosition, new BlockPos(compound.getInteger("spawnX"),compound.getInteger("spawnY"),compound.getInteger("spawnZ")));
		getDataManager().setDirty(spawnPosition);
		
		getDataManager().set(targetPosition, new BlockPos(compound.getInteger("targetX"),compound.getInteger("targetY"),compound.getInteger("targetZ")));
		getDataManager().setDirty(targetPosition);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		if (owner != null){
			compound.setTag("owner", NBTUtil.createUUIDTag(owner));
		}
		compound.setBoolean("tame", getDataManager().get(tame));
		compound.setBoolean("sitting", getDataManager().get(sitting));
		compound.setInteger("variant", getDataManager().get(variant));
		compound.setInteger("spawnX", getDataManager().get(spawnPosition).getX());
		compound.setInteger("spawnY", getDataManager().get(spawnPosition).getY());
		compound.setInteger("spawnZ", getDataManager().get(spawnPosition).getZ());
		compound.setInteger("targetX", getDataManager().get(targetPosition).getX());
		compound.setInteger("targetY", getDataManager().get(targetPosition).getY());
		compound.setInteger("targetZ", getDataManager().get(targetPosition).getZ());
	}
}