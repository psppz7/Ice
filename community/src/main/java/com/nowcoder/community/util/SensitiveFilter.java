package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@Component
public class SensitiveFilter {

    //敏感词替换常量
    private  final String REPLACEMENT = "***" ;
    private TrieNode root = new TrieNode(); //根节点

    @PostConstruct
    public void initTrie()
    {
        try(InputStream is =  this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));)
        {
            String keyWord;
            while ((keyWord = bufferedReader.readLine())!=null)
            {
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addKeyWord(String keyWord)
    {
        char[] chars = keyWord.toCharArray();
        TrieNode trieNode = root;
        for(int i=0;i<chars.length;i++)
        {
            char c = chars[i];
            if(trieNode.getNode(c)==null)
            {
                TrieNode newNode = new TrieNode();
                trieNode.setNode(c,newNode);
                trieNode = newNode;
            }
            else
            {
                trieNode = trieNode.getNode(c);
            }
            if(i==chars.length-1)
                trieNode.setKeyWordEnd(true);
        }
    }

    public String filter(String str)
    {
        StringBuilder stringBuilder = new StringBuilder();
        TrieNode trieNode = root;
        char[] chars = str.toCharArray();

        int p1 = 0;
        int p2 = 0;

        while (p1<chars.length)
        {
            boolean flag = false;
            if(trieNode.getNode(chars[p1])==null) {
                stringBuilder.append(chars[p1]);
                p1++;
                p2 = p1;
            }
            else
            {
                while (trieNode.getNode(chars[p2])!=null||isSymbol(chars[p2]))
                {
                    if(isSymbol(chars[p2]))
                    {
                        p2++;
                        continue;
                    }

                    if(trieNode.getNode(chars[p2]).isKeyWordEnd())
                    {
                        stringBuilder.append(REPLACEMENT);
                        flag = true;
                        break;
                    }
                    trieNode = trieNode.getNode(chars[p2]);
                    p2++;
                }
                trieNode = root;
                if(flag)
                {
                    p2++;
                    p1=p2;
                }
                else
                {
                    stringBuilder.append(chars[p1]);
                    p1++;
                    p2=p1;
                }
            }
        }



        return stringBuilder.toString();
    }
    //判断是否为符号
    private boolean isSymbol(char c)
    {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    //前缀树
    private class TrieNode{

        //是否为底部节点
        private boolean isbottom = false;
        //子节点们
        private Map<Character,TrieNode> subNodes = new HashMap<>();
        //是否为敏感词结尾
        public boolean isKeyWordEnd()
        {
            return isbottom;
        }
        public void setKeyWordEnd(Boolean bool)
        {
            isbottom = bool;
        }
        //生成子节点
        public void setNode(Character c,TrieNode trieNode)
        {
            subNodes.put(c,trieNode);
        }
        //获取某个子节点
        public TrieNode getNode(Character c)
        {
            return subNodes.get(c);
        }
    }

}
