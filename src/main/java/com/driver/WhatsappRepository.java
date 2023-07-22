package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserDb;
    private HashMap<Group, List<Message>> groupMessageDb;
    private HashMap<Message, User> senderDb;
    private HashMap<Group, User> adminDb;
    private HashMap<String,User> userMobileDb;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageDb = new HashMap<Group, List<Message>>();
        this.groupUserDb = new HashMap<Group, List<User>>();
        this.senderDb = new HashMap<Message, User>();
        this.adminDb = new HashMap<Group, User>();
        this.userMobileDb = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(!userMobileDb.containsKey(mobile)){
            User user=new User(name,mobile);
            userMobileDb.put(mobile,user);
            return "SUCCESS";
        }
        else{
            throw new Exception("User already exists");
        }
    }

    public Group createGroup(List<User> users) {
        int n=users.size();
        Group group=new Group();
        if(n==2){
            group.setName(users.get(1).getName());
        }
        else{
            group.setName("Group "+(++customGroupCount));
        }
        group.setNumberOfParticipants(n);
        groupUserDb.put(group,users);
        adminDb.put(group,users.get(0));
        return group;
    }

    public int createMessage(String content) {
        Message message=new Message(++messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        int n=0;
        if(!(groupUserDb.containsKey(group))){
            throw new Exception("Group does not exist");
        }
        if(!(groupUserDb.get(group).contains(sender))){
            throw new Exception("You are not allowed to send message");
        }
        else {
            List<Message> messages=new ArrayList<>();
            if(groupMessageDb.containsKey(group)) {
                messages = groupMessageDb.get(group);
            }
            messages.add(message);
            groupMessageDb.put(group,messages);
            n=messages.size();
            senderDb.put(message,sender);
        }
        return n;
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!(groupUserDb.containsKey(group))){
            throw new Exception("Group does not exist");
        }
        else if(!(adminDb.get(group).equals(approver))){
            throw new Exception("Approver does not have rights");
        }
        else if(!(groupUserDb.get(group).contains(user))){
            throw new Exception("User is not a participant");
        }
        else{
            adminDb.put(group,user);
            return "SUCCESS";
        }
    }

    public int removeUser(User user) throws Exception{

        Group groupuser=new Group();
        for(Group group: groupUserDb.keySet()){
            List<User> userList= groupUserDb.get(group);
            for(User users:userList){
                if(users.equals(user)){
                    groupuser=group;
                    break;
                }
            }
        }
        if(groupuser.getName()==null){
            throw new Exception("User not found");
        }
        else if(adminDb.get(groupuser).equals(user)){
            throw new Exception("Cannot remove admin");
        }
        else {
            List<User> users= groupUserDb.get(groupuser);
            users.remove(user);
            List<Message> messages1=new ArrayList<>();
            for(Message message: senderDb.keySet()){
                if(senderDb.get(message).equals(user)){
                    List<Message> messages= groupMessageDb.get(groupuser);
                    messages.remove(message);
                    messages1.add(message);
                }
            }
            for(Message message:messages1){
                senderDb.remove(message);
            }

        }
        String mobile=user.getMobile();
        userMobileDb.remove(mobile);
        int n=0;
        List<User> usersingroup= groupUserDb.get(groupuser);
        List<Message> messageList= groupMessageDb.get(groupuser);
        n=usersingroup.size()+messageList.size()+ senderDb.size();
        return n;
    }

    public String findMessage(Date start, Date end, int k) throws Exception{
        return "";
    }
}