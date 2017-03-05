Numbered List||(?:^[ ]*[\d]{1,2}(?::|\.)[\t ]+(?:(?!^[ ]*[\d]{1,2}(?::|\.)[\t ]+)(?:[^\t\r\n]+\r?\n))+){2,}||(?:^[ ]*[\d]{1,2}(?::|\.)[\t ]+(?:(?!^[ ]*[\d]{1,2}(?::|\.)[\t ]+)(?:[^\t\r\n]+\r?\n))+)
Alpha Sentence List||(?:^[ ]*[A-Z](?::|\.)+\)?[\t ]+(?:[^\t\n\.]+(?:\.|\n))+\r?\n){2,}||(?:^[ ]*[A-Z](?::|\.)+\)?[\t ]+(?:[^\t\n\.]+(?:\.|\n))+\r?\n)
// Name Value List||(?:^[ ]*[^\t\r\n ]+:[\t ]+(?:[^\t\r\n:]+\r?\n)+){3,}||(?:^[ ]*[^\t\r\n ]+:[\t ]+(?:[^\t\r\n:]+\r?\n)+)
Name Value List||(?:^[^\t\r\n]{2,50}:[\t ]+(?:[^\t\r\n:]+\r?\n)+){3,}||(?:^[^\t\r\n]{2,50}:[\t ]+(?:[^\t\r\n:]+\r?\n)+)
Multi Column List||(?:^(?:[^\t\r\n :]+(?: [^\t\r\n :]+)*(?:\t+| {3,}))+(?:[^\t\r\n ]+(?: [^\t\r\n ]+)*)[\t ]*\r?\n){3,}||\r?\n