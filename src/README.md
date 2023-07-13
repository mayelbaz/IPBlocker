# IPBlocker
IPBlocker is a Kotlin library that allows you to block or allow IP addresses based on their CIDR notations. It provides two data structures, a Trie Tree and a Range List, to efficiently handle IP blocking and allow-listing for different CIDR subnets.

### Pre Assumptions
some pre assumptions were made to fit real-life scenarios of ip blocking

- Current solution only support ipv4
- The list of undesired CIDRs is given by a static file, to fit a real-life scenario of retrieval from an outside source and not in-program created list.
- The allowed subnet size is between 16 and 32.
- If the creation of the blockedIP structure fails, the program ends without checking if the IPs are allowed, showing a relevant error.
- If the isAllowed function fails due to bad input, the program skips the given IP, saying it won't be checked.

### Description

Due to lack of specification of the general use case, and in order to fit a robust solution in a range of use cases, a mixed solution was implemented, 
giving both the option a have a short answer for "isAllowed" usage, and yet hold a not too-large structure for IP looking:
- In the case that subnets are low, giving large ranges of blocked IPs, the range solution is the best fit, and gives small structure to maintain,
But,in this case each look up will take O(N) time - N being the size of the CIDR list.
- In the case where we want to have a fast answer for "isAllowed", we would like to have a quick search through the blocked IPs, and not 
go over each range. In this case the trieTree solution is best, giving a constant time look up of each ip.
But,in this case we might hold a large tree, maximum 256^4 size, with a high building time.
- The mix solution holds both trie tree and range list, and allows the user to change it by the requirements.
- For each CIDR, if the subnet is larger than 16 (mid-size subnet), it'll go in the tree, else - to the range list. never both.
- The isAllowed function first looks at the tree (fast search) and then at the range list.

### Features

- Block or allow IP addresses based on CIDR notation.
- Efficient lookup using Trie Tree and Range List data structures.
- Supports CIDR subnets with a subnet mask of 16 or higher using the Trie Tree (small ranges).
- Supports CIDR subnets with a subnet mask of less than 16 using the Range List (large ranges).

### Usage

1. Create an instance of the IPBlocker class
2. Add CIDRs to the CIDR list - currently called cidr_list.txt under src/main/resources.
3. Load blocked IP addresses using CIDR notation the blocker, using loadBlockedIPsFromFile func.
4. Check if an IP address is allowed, using the isAllowed function.

### File Formats

cidr_list.txt: This file contains a list of blocked IP addresses in CIDR notation, with each CIDR subnet on a separate line. Example:
192.168.0.0/24
10.0.0.0/16

### Testing

Though test were not required, and therefore not created for this project, future tests can be created in this manner:
- A test cidr_list.txt file with various CIDRs for integration tests
- Unit testing fot IPBlocker and IPHelper to check correctness of functions, such as ip and subnet parsing. 
    Also, correctness of thrown error values.
- Checking both trie tree and range list hold correct values of ips/ranges

### Future developments
- Implementing a range merging system for the range list.
- Better error handling
- A system to automatically recieve IPs to check.
