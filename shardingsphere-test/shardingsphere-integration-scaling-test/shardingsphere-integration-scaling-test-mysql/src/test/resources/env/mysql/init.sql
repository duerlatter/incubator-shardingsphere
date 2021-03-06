--
-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY '123456';
GRANT All privileges ON *.* TO 'root'@'%';

CREATE DATABASE ds_src;
CREATE TABLE ds_src.t1(id INT PRIMARY KEY AUTO_INCREMENT, C1 INT NOT NULL, C2 VARCHAR(255) NOT NULL);
CREATE DATABASE ds_dst;
CREATE TABLE ds_dst.t1(id INT PRIMARY KEY AUTO_INCREMENT, C1 INT NOT NULL, C2 VARCHAR(255) NOT NULL);
