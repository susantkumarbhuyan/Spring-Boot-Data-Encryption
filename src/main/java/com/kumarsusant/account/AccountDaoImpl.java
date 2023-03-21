package com.kumarsusant.account;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.kumarsusant.commonclasses.User;
import com.kumarsusant.constants.SchemaConstants;
import com.kumarsusant.util.MongoDBUtil;

@Repository("accountDao")
public class AccountDaoImpl implements IAccountDao {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public User registerUser(User user) {
		System.out.println("DB Name :" + mongoTemplate.getDb().getName());
		mongoTemplate.save(user, "NewEncryptData");
		Document ob = new Document();
		ob.append("_id", user.getId());
		ob.append("name", user.getName());
		ob.append("dob", user.getDob());
		ob.append("phoneNo", user.getPhoneNo());
		// MongoDBUtil.getMongoDataBase(SchemaConstants.USER_SCHEMA).getCollection("EncryptData").insertOne(ob);
		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(user.getId()));
		;
		Document find = new Document("_id", user.getId());
		System.out.println("User Name :" + MongoDBUtil.getMongoDataBase(SchemaConstants.USER_SCHEMA)
				.getCollection("EncryptData").find(find).first().toJson());
		Document data = MongoDBUtil.getMongoDataBase(SchemaConstants.USER_SCHEMA).getCollection("EncryptData")
				.find(find).first();
		return new User(data.getString("_id"), data.getString("name"), data.getInteger("dob", 0),
				data.getLong("phoneNo"));
//		User user1v=	mongoTemplate.find(query, User.class, "HSUserDB").get(0);
//		System.out.println( "NAme" +user1v.toString());
	}

	@Override
	public User getUser(String id) {
		Document find = new Document("_id", id);
		System.out.println("User Name :" + MongoDBUtil.getMongoDataBase(SchemaConstants.USER_SCHEMA)
				.getCollection("EncryptData").find(find).first().toJson());
		Document data = MongoDBUtil.getMongoDataBase(SchemaConstants.USER_SCHEMA).getCollection("EncryptData")
				.find(find).first();
		return new User(data.getString("_id"), data.getString("name"), data.getInteger("dob", 0),
				data.getLong("phoneNo"));
	}

}
//List<Bson> pipeline = Arrays.asList(
//        Aggregates.match(Filters.eq("empId", 234)),
//        Aggregates.unwind("$employeePocMappingList"),
//        Aggregates.unwind("$employeePocMappingList.roleIdList", new UnwindOptions().preserveNullAndEmptyArrays(true)),
//        Aggregates.lookup("poc_details", "employeePocMappingList.pocId", "pocId", "pocDetails"),
//        Aggregates.unwind("$pocDetails", new UnwindOptions().preserveNullAndEmptyArrays(true))
//    );
// 
//    Aggregation aggregation = Aggregation.newAggregation(
//            Aggregation.match(Criteria.where("empId").is(2)),
//            Aggregation.unwind("employeePocMappingList"),
//            Aggregation.unwind("employeePocMappingList.roleIdList",true),
//            Aggregation.lookup("poc_details", "employeePocMappingList.pocId", "pocId", "pocDetails"),
//            Aggregation.unwind("pocDetails", false)
//        );
//    //.aggregate("{$match:{empId:#}}", empId)
////	.and("{'$unwind':{'path':'$employeePocMappingList','preserveNullAndEmptyArrays':false}}")
////	.and("{'$unwind' : { 'path' : '$employeePocMappingList.roleIdList' , 'preserveNullAndEmptyArrays' : true}}")
////	.and("{$lookup:{from:#,localField:'employeePocMappingList.pocId',foreignField:'pocId',as:'pocDetails'}}",
////			MongoCollectionConstants.CN_POCDETAILS)
////	.and("{ '$unwind' : { 'path' : '$pocDetails' , 'preserveNullAndEmptyArrays' : true}}")
//    System.out.println("MongoTemple Aggregation Query "+aggregation.toString());       
//    System.out.println("MongoClient Aggregation Query "+pipeline.toString())